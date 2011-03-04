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

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Savepoint
import java.util.concurrent._
import java.util.concurrent.atomic._
import java.util.{UUID => JavaUUID}

import org.postgis._
import org.postgresql.PGConnection
import org.postgresql.util.PGmoney
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState

import com.netcapital.util._

/**
 * An example PostgreSQL adapter for Pool Party's Connection Factory. This example uses
 * PostGIS extensions, and uses Configgy to get database connection parameters.
 *
 * It also provides a wrapper for UUID objects with Java's UUID.
 */
object PgAdapter extends Logging with Configed {
  import org.postgis.PGgeometry
  import org.postgresql.util.PGobject

  def init = DatabasePool.registerConnectionFactory(mkConnection)

  /**
   * Creates a connection to the PostgreSQL database and registers
   * mapping extensions.
   *
   * NOTE: This connection factory not add it to the pool.
   */
  private def mkConnection():Connection = {

    val uri = "jdbc:postgresql_postGIS://%s:%d/%s?loglevel=%d".format(
      config.getString("database.host").get,
      config.getInt("database.port").get,
      config.getString("database.name").get,
      config.getInt("database.log_level").get)
    val user = config.getString("database.user").get
    val password = config.getString("database.password").get

    Class.forName("org.postgis.DriverWrapper")
    val connection = java.sql.DriverManager.getConnection(uri, user, password)
    // Register Postgres extensions
    List(
      ("uuid", classOf[PGuuid]),
      ("money", classOf[org.postgresql.util.PGmoney])
    ).foreach { case (name, classPath) =>
      connection.asInstanceOf[PGConnection].addDataType(name, classPath)
    }

    // Test geometries
    val s = connection.createStatement
    val rs = s.executeQuery("SELECT 'POINT(1 2)'::geometry")
    rs.next
    val result = rs.getObject(1).asInstanceOf[PGobject]
    if (result.isInstanceOf[PGgeometry] &&
        result.asInstanceOf[PGgeometry].getGeometry.isInstanceOf[Point]) {
      // log.info("PGgeometry successful!")
    } else {
      try {
        rs.close
        s.close
        connection.close
      } catch { case _ => }
      throw new Exception("PGgeometry test failed: is PostGIS JDBC & Server support installed")
    }
    rs.close
    s.close

    return connection
  }
}

class PGuuid extends org.postgresql.util.PGobject with Logging {
  private var uuid:JavaUUID = null
  setType("uuid")

  def this(s:String) = {
    this()
    setValue(s)
  }

  def this(_uuid:JavaUUID) = {
    this()
    uuid = _uuid
  }

  override def setValue(s:String):Unit = {
    try {
      uuid = JavaUUID.fromString(s)
    } catch {
      case e:Throwable =>
        throw new PSQLException("Invalid UUID", PSQLState.INVALID_PARAMETER_VALUE, e)
    }
  }

  override def getValue():String = { uuid.toString }

  def getUuid:JavaUUID = uuid
  def setUuid(value:JavaUUID):Unit = { uuid = value }
}
