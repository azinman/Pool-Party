// Copyright (C) 2011 by NetCapital LLC
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.netcapital.poolparty


import scala.collection.mutable
import java.util.Calendar
import java.util.Date
import java.util.concurrent._
import java.util.concurrent.atomic._
import org.scala_tools.time._
import org.scala_tools.time.Imports._

import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.sql._

import com.netcapital.util._

/**
 * The main object ot interact with. Clients use this database pool implementation using the
 * following scala-friendly syntax:
 *
 * <pre>
 * def myfunc = {
 *   DatabasePool.borrow { (conn) =>
 *     var s = conn.prepareStatement("select hello from world where a = ?") // Cached on second call
 *     s.setString(1, "my parameter")
 *     val rs = s.executeQuery
 *     val results = mutable.Buffer[String]()
 *     while (rs.next) {
 *       results += rs.getString("hello")
 *     }
 *     rs.close // Happens automatically if omitted providing exception-robustness
 *     results.toSeq // Anonymous function is typed to whatever this returns
 *   }
 * }
 * </pre>
*/




object DatabasePool extends Logging with Configed {
  // Internal state --------------------------------------------------------------------------------
  private var connectionsBuilt = new AtomicInteger(0)

  private val connections = new PriorityBlockingQueue[DbConnection]()

  /**
   * This is a private helper class to keep references to database connections with
   * a reference count of usage. This is useful for when a given thread continues
   * to borrow connections in its call stack, so the functions do not need to pass
   * around references to the database connection.
   */
  private[this] case class _ThreadLocalConnection(var connOpt:Option[DbConnection], var refCount:Int=0)

  private val threadLocalConnection = new ThreadLocal(_ThreadLocalConnection(None))

  private var connectionFactory:Option[() => Connection] = None

  // Public functions ------------------------------------------------------------------------------

  /**
   * This pool must be initalized by registering the connection factory method. It should take
   * no arguments and return a JDBC Connection object
   */
  def registerConnectionFactory(factoryMethod:(() => Connection)) = {
    connectionFactory = Some(factoryMethod)
  }

  /**
   * Take a live connection from the database pool and pass it to
   * the passed callback.
   *
   * When the callball is finished executing the connection will
   * be returned to the pool.
   */
  def borrow[T](callback:(Connection=>T)):T = {
    // See if this thread already has a connection
    val localConnObj = threadLocalConnection.get
    var connObj:DbConnection = localConnObj.connOpt match {
      case Some(lConnObj:DbConnection) => {
        log.trace("Reusing existing thread-local connection")
        lConnObj
      }
      case None => {
        // We need to take it from the pool / create one
        val lConnObj = _borrowFromPool
        localConnObj.connOpt = Some(lConnObj)
        log.trace("No thread-local connection, polled from pool")
        lConnObj
      }
    }
    localConnObj.refCount += 1
    log.trace("Got connObj %s with refCount %d", connObj, localConnObj.refCount)
    val conn = connObj.get
    // Save our current state if this thread is already using the connection
    // (do not roll back their potential changes)
    val savepointOpt:Option[Savepoint] = localConnObj.refCount > 1 match {
      case true => Some(conn.setSavepoint)
      case false => None
    }
    // Finally, we can give our function the pooled connection
    // Put in a try/catch block so that we can be sure to put back the connection before
    // rethrowing the error.
    var thrownException:Option[Throwable] = None
    var callbackResult:Option[T] = None
    try {
      callbackResult = Some(callback(conn))
    } catch {
      // TODO check if the connection is closed, and react appropriately
      case e:Throwable => {
        thrownException = Some(e)
        log.fatal(e, "Exception thrown in callback")
      }
    } finally {
      log.trace("In finally loop.. closing all temp objects")
      conn.closeAllTemporaryObjects
      localConnObj.refCount -= 1
      // Verify we are in a good state
      assert(!conn.isClosed)
      // Auto-commit is not supported; it fucks with savepoints. This assertion is implicit
      // because we disable setting autocommit in our connection proxy.
      // assert(!conn.getAutoCommit)

      // If we have a saved point, then restore to that. Otherwise our refCount is at 0
      // and we should throw it back into the pool in a default state.
      savepointOpt match {
        case Some(savepoint:Savepoint) =>
          log.trace("Rolling back to saved point: connObj %s", connObj)
          try { conn.rollback(savepoint) } catch { case _ => }
        case None =>
          assert(localConnObj.refCount == 0)
          try { conn.rollback } catch { case _ => }
          conn.clearWarnings
          localConnObj.connOpt = None // We are done with our thread-local version
          // TODO Check to make sure too many conns haven't been built.
          // Now return it to the pool
          log.trace("Putting back connObj %s", connObj)
          connections.put(connObj)
      }
    } // end finally

    thrownException match {
      case Some(e:Throwable) => throw e
      case None =>
    }
    callbackResult.get
  }

  /**
   * Create (and insert) as many connections as necessary to meet
   * the minimum required in the database pool.
   **/
  def assureMinimumConnections():Unit = {
    val MIN_CONNECTIONS = config.getInt("database.min_conns").get
    if (connectionsBuilt.get < MIN_CONNECTIONS) {
      val delta = MIN_CONNECTIONS - connectionsBuilt.get
      log.info("Creating %d connections to DB (assuring minimum)", delta)
      1 to delta foreach { (num) =>
        connectionsBuilt.incrementAndGet
        val connObj = new DbConnection(mkPooledConnection)
        if (!connections.offer(connObj)) {
          // Connection pool is full, close this one and ignore
          try { connObj._conn.close } catch { case _ => }
          connectionsBuilt.decrementAndGet
        }
      }
    }
  }

  // Private functions -----------------------------------------------------------------------------

  /**
   * Private helper that either takes a connection from the pool (potentially blocking)
   * or creates a new one, depending on if we have hit the maximum number of connections or not
   */
  private def _borrowFromPool:DbConnection = {
    var connObj:DbConnection = connections.poll
    if (connObj == null) {
      val MAX_CONNECTIONS = config.getInt("database.max_conns").get
      if (connectionsBuilt.get >= MAX_CONNECTIONS) {
        connObj = connections.take // blocks until avail
      } else {
        // Try to build a conn
        val connsBuilt = connectionsBuilt.incrementAndGet
        if (connsBuilt <= MAX_CONNECTIONS) {
          connObj = new DbConnection(mkPooledConnection)
        } else {
          // Another thread beat us to it, just grab it
          assert(connectionsBuilt.decrementAndGet == MAX_CONNECTIONS)
          connObj = connections.take // blocks until avail
        }
      }
    }
    connObj
  }

  /**
   * Make a proxy'd JDBC connection using the registered factory method, and disable
   * auto-commit which is not supported.
   */
  private def mkPooledConnection:PooledConnection = {
    val connection:Connection = connectionFactory.get()
    // Auto-commit is not supported
    connection.setAutoCommit(false)
    return new PooledConnection(connection)
  }

  /**
   * Wrapper for PooledConnections for the internal pool. Ensures that the connection is still
   * alive, and if it is not, makes a new one. It also keeps track of the last time it is used,
   * which is useful for sorting connections so that the oldest gets used next in a priority queue.
   */
  private[this] class DbConnection(var _conn:PooledConnection)
  extends Logging with Comparable[DbConnection] {
    var lastUse = DateTime.now

    def compareTo(connObj:DbConnection):Int = lastUse.compareTo(connObj.lastUse)

    def get:PooledConnection = {
      try {
        // Make sure it's valid (still open)
        // This will be memoized in _conn
        _conn.prepareStatement("select 1").execute
      } catch {
        case _ => {
          log.info("Reopening closed JDBC Connection")
          try { _conn.reallyClose } catch { case _ => }
          _conn = DatabasePool.mkPooledConnection
        }
      }

      lastUse = DateTime.now
      return _conn
    }
  }
}

