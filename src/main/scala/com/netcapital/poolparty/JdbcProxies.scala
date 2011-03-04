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
 * This file proxies the main JDBC classes that need to be wrapped -- Connections,
 * PreparedStatements, and ResultSets.
 *
 * The proxies serve three purposes:
 *   1. Make sure connections are not actually closed
 *   2. Cache PreparedStatements (proxies)
 *   2. Keep track of child objects for Exception-resistent clean up.
 *
 */

class ResultSetProxy(val rs:ResultSet, ps:PreparedStatementProxy) extends ResultSet with Proxy {
  def self:ResultSet = rs

  def close = {
    try { self.close }
    finally { ps.resultSetProxyClosed(this) }
  }

  override def toString:String = "[ResultSetProxy self=%s]".format(self)

  def absolute(row:Int):Boolean = self.absolute(row)
  def afterLast = self.afterLast
  def beforeFirst = self.beforeFirst
  def cancelRowUpdates = self.cancelRowUpdates
  def clearWarnings = self.clearWarnings
  def deleteRow = self.deleteRow
  def findColumn(columnName:String):Int = self.findColumn(columnName)
  def first:Boolean = self.first
  def getArray(i:Int):java.sql.Array = self.getArray(i)
  def getArray(colName:String):java.sql.Array = self.getArray(colName)
  def getAsciiStream(columnIndex:Int):InputStream = self.getAsciiStream(columnIndex)
  def getAsciiStream(columnName:String):InputStream = self.getAsciiStream(columnName)
  def getBigDecimal(columnIndex:Int):java.math.BigDecimal = self.getBigDecimal(columnIndex)
  @SuppressWarnings(scala.Array[String]("deprecation")) // Why doesn't this wor?
  def getBigDecimal(columnIndex:Int, scale:Int):java.math.BigDecimal = self.getBigDecimal(columnIndex, scale)
  def getBigDecimal(columnName:String):java.math.BigDecimal = self.getBigDecimal(columnName)
  @SuppressWarnings(scala.Array[String]("deprecation"))
  def getBigDecimal(columnName:String, scale:Int):java.math.BigDecimal = self.getBigDecimal(columnName, scale)
  def getBinaryStream(columnIndex:Int):InputStream = self.getBinaryStream(columnIndex)
  def getBinaryStream(columnName:String):InputStream = self.getBinaryStream(columnName)
  def getBlob(i:Int):Blob = self.getBlob(i)
  def getBlob(colName:String):Blob = self.getBlob(colName)
  def getBoolean(columnIndex:Int):Boolean = self.getBoolean(columnIndex)
  def getBoolean(columnName:String):Boolean = self.getBoolean(columnName)
  def getByte(columnIndex:Int):Byte = self.getByte(columnIndex)
  def getByte(columnName:String):Byte = self.getByte(columnName)
  def getBytes(columnIndex:Int):scala.Array[Byte] = self.getBytes(columnIndex)
  def getBytes(columnName:String):scala.Array[Byte] = self.getBytes(columnName)
  def getCharacterStream(columnIndex:Int):Reader = self.getCharacterStream(columnIndex)
  def getCharacterStream(columnName:String):Reader = self.getCharacterStream(columnName)
  def getClob(i:Int):Clob = self.getClob(i)
  def getClob(colName:String):Clob = self.getClob(colName)
  def getConcurrency:Int = self.getConcurrency
  def getCursorName:String = self.getCursorName
  def getDate(columnIndex:Int):java.sql.Date = self.getDate(columnIndex)
  def getDate(columnIndex:Int, cal:Calendar):java.sql.Date = self.getDate(columnIndex, cal)
  def getDate(columnName:String):java.sql.Date = self.getDate(columnName)
  def getDate(columnName:String, cal:Calendar):java.sql.Date = self.getDate(columnName, cal)
  def getDouble(columnIndex:Int):Double = self.getDouble(columnIndex)
  def getDouble(columnName:String):Double = self.getDouble(columnName)
  def getFetchDirection:Int = self.getFetchDirection
  def getFetchSize:Int = self.getFetchSize
  def getFloat(columnIndex:Int):Float = self.getFloat(columnIndex)
  def getFloat(columnName:String):Float = self.getFloat(columnName)
  def getInt(columnIndex:Int):Int = self.getInt(columnIndex)
  def getInt(columnName:String):Int = self.getInt(columnName)
  def getLong(columnIndex:Int):Long = self.getLong(columnIndex)
  def getLong(columnName:String):Long = self.getLong(columnName)
  def getMetaData:ResultSetMetaData = self.getMetaData
  def getObject(columnIndex:Int):Object = self.getObject(columnIndex)
  def getObject(i:Int, map:java.util.Map[String, Class[_]]):Object = self.getObject(i, map)
  def getObject(columnName:String):Object = self.getObject(columnName)
  def getObject(colName:String, map:java.util.Map[String, Class[_]]):Object = self.getObject(colName, map)
  def getRef(i:Int):Ref = self.getRef(i)
  def getRef(colName:String):Ref = self.getRef(colName)
  def getRow:Int = self.getRow
  def getShort(columnIndex:Int):Short = self.getShort(columnIndex)
  def getShort(columnName:String):Short = self.getShort(columnName)
  def getStatement:Statement = self.getStatement
  def getString(columnIndex:Int):String = self.getString(columnIndex)
  def getString(columnName:String):String = self.getString(columnName)
  def getTime(columnIndex:Int):Time = self.getTime(columnIndex)
  def getTime(columnIndex:Int, cal:Calendar):Time = self.getTime(columnIndex, cal)
  def getTime(columnName:String):Time = self.getTime(columnName)
  def getTime(columnName:String, cal:Calendar):Time = self.getTime(columnName, cal)
  def getTimestamp(columnIndex:Int):Timestamp = self.getTimestamp(columnIndex)
  def getTimestamp(columnIndex:Int, cal:Calendar):Timestamp = self.getTimestamp(columnIndex, cal)
  def getTimestamp(columnName:String):Timestamp = self.getTimestamp(columnName)
  def getTimestamp(columnName:String, cal:Calendar):Timestamp = self.getTimestamp(columnName, cal)
  def getType:Int = self.getType
  @SuppressWarnings(scala.Array[String]("deprecation"))
  def getUnicodeStream(columnIndex:Int):InputStream = self.getUnicodeStream(columnIndex)
  @SuppressWarnings(scala.Array[String]("deprecation"))
  def getUnicodeStream(columnName:String):InputStream = self.getUnicodeStream(columnName)
  def getURL(columnIndex:Int):URL = self.getURL(columnIndex)
  def getURL(columnName:String):URL = self.getURL(columnName)
  def getWarnings:SQLWarning = self.getWarnings
  def insertRow = self.insertRow
  def isAfterLast:Boolean = self.isAfterLast
  def isBeforeFirst:Boolean = self.isBeforeFirst
  def isFirst:Boolean = self.isFirst
  def isLast:Boolean = self.isLast
  def last:Boolean = self.last
  def moveToCurrentRow = self.moveToCurrentRow
  def moveToInsertRow = self.moveToInsertRow
  def next:Boolean = self.next
  def previous:Boolean = self.previous
  def refreshRow = self.refreshRow
  def relative(rows:Int):Boolean = self.relative(rows)
  def rowDeleted:Boolean = self.rowDeleted
  def rowInserted:Boolean = self.rowInserted
  def rowUpdated:Boolean = self.rowUpdated
  def setFetchDirection(direction:Int) = self.setFetchDirection(direction)
  def setFetchSize(rows:Int) = self.setFetchSize(rows)
  def updateArray(columnIndex:Int, x:java.sql.Array) = self.updateArray(columnIndex, x)
  def updateArray(columnName:String, x:java.sql.Array) = self.updateArray(columnName, x)
  def updateAsciiStream(columnIndex:Int, x:InputStream, length:Int) = self.updateAsciiStream(columnIndex, x, length)
  def updateAsciiStream(columnName:String, x:InputStream, length:Int) = self.updateAsciiStream(columnName, x, length)
  def updateBigDecimal(columnIndex:Int, x:java.math.BigDecimal) = self.updateBigDecimal(columnIndex, x)
  def updateBigDecimal(columnName:String, x:java.math.BigDecimal) = self.updateBigDecimal(columnName, x)
  def updateBinaryStream(columnIndex:Int, x:InputStream, length:Int) = self.updateBinaryStream(columnIndex, x, length)
  def updateBinaryStream(columnName:String, x:InputStream, length:Int) = self.updateBinaryStream(columnName, x, length)
  def updateBlob(columnIndex:Int, x:Blob) = self.updateBlob(columnIndex, x)
  def updateBlob(columnName:String, x:Blob) = self.updateBlob(columnName, x)
  def updateBoolean(columnIndex:Int, x:Boolean) = self.updateBoolean(columnIndex, x)
  def updateBoolean(columnName:String, x:Boolean) = self.updateBoolean(columnName, x)
  def updateByte(columnIndex:Int, x:Byte) = self.updateByte(columnIndex, x)
  def updateByte(columnName:String, x:Byte) = self.updateByte(columnName, x)
  def updateBytes(columnIndex:Int, x:scala.Array[Byte]) = self.updateBytes(columnIndex, x)
  def updateBytes(columnName:String, x:scala.Array[Byte]) = self.updateBytes(columnName:String, x)
  def updateCharacterStream(columnIndex:Int, x:Reader, length:Int) = self.updateCharacterStream(columnIndex, x, length)
  def updateCharacterStream(columnName:String, reader:Reader) = self.updateCharacterStream(columnName, reader)
  def updateCharacterStream(columnName:String, reader:Reader, length:Int) = self.updateCharacterStream(columnName, reader, length)
  def updateClob(columnIndex:Int, x:Clob) = self.updateClob(columnIndex, x)
  def updateClob(columnIndex:Int, reader:Reader) = self.updateClob(columnIndex, reader)
  def updateClob(columnIndex:Int, reader:Reader, length:Long) = self.updateClob(columnIndex, reader, length)
  def updateClob(columnName:String, x:Clob) = self.updateClob(columnName, x)
  def updateClob(columnName:String, reader:Reader) = self.updateClob(columnName, reader)
  def updateClob(columnName:String, reader:Reader, length:Long) = self.updateClob(columnName, reader, length)
  def updateDate(columnIndex:Int, x:java.sql.Date) = self.updateDate(columnIndex, x)
  def updateDate(columnName:String, x:java.sql.Date) = self.updateDate(columnName, x)
  def updateDouble(columnIndex:Int, x:Double) = self.updateDouble(columnIndex, x)
  def updateDouble(columnName:String, x:Double) = self.updateDouble(columnName, x)
  def updateFloat(columnIndex:Int, x:Float) = self.updateFloat(columnIndex, x)
  def updateFloat(columnName:String, x:Float) = self.updateFloat(columnName, x)
  def updateInt(columnIndex:Int, x:Int) = self.updateInt(columnIndex, x)
  def updateInt(columnName:String, x:Int) = self.updateInt(columnName, x)
  def updateLong(columnIndex:Int, x:Long) = self.updateLong(columnIndex, x)
  def updateLong(columnName:String, x:Long) = self.updateLong(columnName, x)
  def updateNCharacterStream(columnIndex:Int, x:Reader) = self.updateNCharacterStream(columnIndex, x)
  def updateNCharacterStream(columnIndex:Int, x:Reader, length:Long) = self.updateNCharacterStream(columnIndex, x, length)
  def updateNCharacterStream(columnLabel:String, reader:Reader) = self.updateNCharacterStream(columnLabel, reader)
  def updateNCharacterStream(columnLabel:String, reader:Reader, length:Long) = self.updateNCharacterStream(columnLabel, reader, length)
  def updateNClob(columnIndex:Int, nClob:NClob) = self.updateNClob(columnIndex, nClob)
  def updateNClob(columnIndex:Int, reader:Reader) = self.updateNClob(columnIndex, reader)
  def updateNClob(columnIndex:Int, reader:Reader, length:Long) = self.updateNClob(columnIndex, reader)
  def updateNClob(columnLabel:String, nClob:NClob) = self.updateNClob(columnLabel, nClob)
  def updateNClob(columnLabel:String, reader:Reader) = self.updateNClob(columnLabel, reader)
  def updateNClob(columnLabel:String, reader:Reader, length:Long) = self.updateNClob(columnLabel, reader, length)
  def updateNString(columnIndex:Int, nString:String) = self.updateNString(columnIndex, nString)
  def updateNString(columnLabel:String, nString:String) = self.updateNString(columnLabel, nString)
  def updateNull(columnIndex:Int) = self.updateNull(columnIndex)
  def updateNull(columnName:String) = self.updateNull(columnName)
  def updateObject(columnIndex:Int, x:Object) = self.updateObject(columnIndex, x)
  def updateObject(columnIndex:Int, x:Object, scale:Int) = self.updateObject(columnIndex, x, scale)
  def updateObject(columnName:String, x:Object) = self.updateObject(columnName, x)
  def updateObject(columnName:String, x:Object, scale:Int) = self.updateObject(columnName, x, scale)
  def updateRef(columnIndex:Int, x:Ref) = self.updateRef(columnIndex, x)
  def updateRef(columnName:String, x:Ref) = self.updateRef(columnName, x)
  def updateRow = self.updateRow
  def updateShort(columnIndex:Int, x:Short) = self.updateShort(columnIndex, x)
  def updateShort(columnName:String, x:Short) = self.updateShort(columnName, x)
  def updateString(columnIndex:Int, x:String) = self.updateString(columnIndex, x)
  def updateString(columnName:String, x:String) = self.updateString(columnName, x)
  def updateTime(columnIndex:Int, x:Time) = self.updateTime(columnIndex, x)
  def updateTime(columnName:String, x:Time) = self.updateTime(columnName, x)
  def updateTimestamp(columnIndex:Int, x:Timestamp) = self.updateTimestamp(columnIndex, x)
  def updateTimestamp(columnName:String, x:Timestamp) = self.updateTimestamp(columnName, x)
  def wasNull:Boolean = self.wasNull
  // JDK 6
  def updateBlob(x1:String, x2:InputStream) = self.updateBlob(x1, x2)
  def updateBlob(x1:Int, x2:InputStream) = self.updateBlob(x1, x2)
  def updateBinaryStream(x1:String, x2:InputStream) = self.updateBinaryStream(x1, x2)
  def updateAsciiStream(x1:String, x2:InputStream) = self.updateAsciiStream(x1, x2)
  def updateCharacterStream(x1:Int, x2:Reader) = self.updateCharacterStream(x1, x2)
  def updateBinaryStream(x1:Int, x2:InputStream) = self.updateBinaryStream(x1, x2)
  def updateAsciiStream(x1:Int, x2:InputStream) = self.updateAsciiStream(x1, x2)
  def updateBlob(x1:String, x2:InputStream, x3:Long) = self.updateBlob(x1, x2, x3)
  def updateBlob(x1:Int, x2:InputStream, x3:Long) = self.updateBlob(x1, x2, x3)
  def updateCharacterStream(x1:String, x2:Reader, x3:Long) = self.updateCharacterStream(x1, x2, x3)
  def updateBinaryStream(x1:String, x2:InputStream, x3:Long) = self.updateBinaryStream(x1, x2, x3)
  def updateAsciiStream(x1:String, x2:InputStream, x3:Long) = self.updateAsciiStream(x1, x2, x3)
  def updateCharacterStream(x1:Int, x2:Reader, x3:Long) = self.updateCharacterStream(x1, x2, x3)
  def updateBinaryStream(x1:Int, x2:InputStream, x3:Long) = self.updateBinaryStream(x1, x2, x3)
  def updateAsciiStream(x1:Int, x2:InputStream, x3:Long) = self.updateAsciiStream(x1, x2, x3)
  def getNCharacterStream(x1:String):Reader = self.getNCharacterStream(x1)
  def getNCharacterStream(x1:Int):Reader = self.getNCharacterStream(x1)
  def getNString(x1:String):String = self.getNString(x1)
  def getNString(x1:Int):String = self.getNString(x1)
  def updateSQLXML(x1:String, x2:java.sql.SQLXML) = self.updateSQLXML(x1, x2)
  def updateSQLXML(x1:Int, x2:java.sql.SQLXML) = self.updateSQLXML(x1, x2)
  def getSQLXML(x1:String):SQLXML = self.getSQLXML(x1)
  def getSQLXML(x1:Int):SQLXML = self.getSQLXML(x1)
  def getNClob(x1:String):NClob = self.getNClob(x1)
  def getNClob(x1:Int):NClob = self.getNClob(x1)
  def isClosed:Boolean = self.isClosed
  def getHoldability:Int = self.getHoldability
  def updateRowId(x1:String, x2:java.sql.RowId) = self.updateRowId(x1, x2)
  def updateRowId(x1:Int, x2:java.sql.RowId) = self.updateRowId(x1, x2)
  def getRowId(x1:String):RowId = self.getRowId(x1)
  def getRowId(x1:Int):RowId = self.getRowId(x1)
  def isWrapperFor(iface:java.lang.Class[_]):Boolean = self.isWrapperFor(iface)
  def unwrap[T](iface:java.lang.Class[T]):T = self.unwrap(iface)
}

class PreparedStatementProxy(val preparedStatment:PreparedStatement) extends PreparedStatement with Proxy with Logging {
  def self:PreparedStatement = preparedStatment

  // TRACKED INTERACTIONS --------------------------------------------------------------------------
  lazy private val openResultSets:mutable.ListBuffer[ResultSetProxy] = mutable.ListBuffer()

  def executeQuery:ResultSet = {
    val rs = new ResultSetProxy(self.executeQuery, this)
    log.trace("result set proxy added")
    openResultSets.prepend(rs)
    rs
  }

  def executeQuery(query:String):ResultSet = {
    val rs = new ResultSetProxy(self.executeQuery(query), this)
    log.trace("result set proxy added")
    openResultSets.prepend(rs)
    rs
  }

  def closeOpenResultSets:Unit = {
    for (rs <- openResultSets) {
      log.trace("closing open result set")
      try { rs.close } catch { case _ => }
    }
    openResultSets.clear
    try { clearParameters } catch { case _ => }
  }

  def resultSetProxyClosed(rs:ResultSetProxy):Unit = {
    log.trace("result set proxy closed")
    openResultSets -= rs
  }


  def execute(sql:String, autoGeneratedKeys:Int):Boolean = self.execute(sql, autoGeneratedKeys)
  def execute(x1:String, x2:scala.Array[String]):Boolean = self.execute(x1, x2)
  def execute(x1:String, x2:scala.Array[Int]):Boolean = self.execute(x1, x2)
  def executeUpdate(x1:String, x2:scala.Array[String]):Int = self.executeUpdate(x1, x2)
  def executeUpdate(x1:String, x2:scala.Array[Int]):Int = self.executeUpdate(x1, x2)
  def executeUpdate(x1:String, x2:Int):Int = self.executeUpdate(x1, x2)
  def execute(x1:String):Boolean = self.execute(x1)
  def executeUpdate(x1:String):Int = self.executeUpdate(x1)

  def isClosed:Boolean = self.isClosed
  def getResultSet:ResultSet = self.getResultSet
  def cancel = self.cancel
  def close = self.close


  override def toString:String = "[PreparedStatementProxy self=%s]".format(self)

  // UNTRACKED INTERACTIONS ------------------------------------------------------------------------
  def execute:Boolean = self.execute
  def executeUpdate:Int = self.executeUpdate

  def getMetaData = self.getMetaData
  def getParameterMetaData = self.getParameterMetaData
  def addBatch = self.addBatch
  def clearParameters = self.clearParameters


  def setArray(i:Int, x:java.sql.Array) = self.setArray(i, x)
  def setAsciiStream(parameterIndex:Int, x:InputStream, length:Int) = self.setAsciiStream(parameterIndex, x, length)
  def setBigDecimal(parameterIndex:Int, x:java.math.BigDecimal) = self.setBigDecimal(parameterIndex, x)
  def setBinaryStream(parameterIndex:Int, x:InputStream, length:Int) = self.setBinaryStream(parameterIndex, x, length)
  def setBlob(i:Int, x:Blob) = self.setBlob(i, x)
  def setBoolean(parameterIndex:Int, x:Boolean) = self.setBoolean(parameterIndex, x)
  def setByte(parameterIndex:Int, x:Byte) = self.setByte(parameterIndex, x)
  def setBytes(parameterIndex:Int, x:scala.Array[Byte]) = self.setBytes(parameterIndex, x)
  def setCharacterStream(parameterIndex:Int, reader:Reader, length:Int) = self.setCharacterStream(parameterIndex, reader, length)
  def setClob(i:Int, x:Clob) = self.setClob(i, x)
  def setDate(parameterIndex:Int, x:java.sql.Date) = self.setDate(parameterIndex, x)
  def setDate(parameterIndex:Int, x:java.sql.Date, cal:Calendar) = self.setDate(parameterIndex, x, cal)
  def setDouble(parameterIndex:Int, x:Double) = self.setDouble(parameterIndex, x)
  def setFloat(parameterIndex:Int, x:Float) = self.setFloat(parameterIndex, x)
  def setInt(parameterIndex:Int, x:Int) = self.setInt(parameterIndex, x)
  def setLong(parameterIndex:Int, x:Long) = self.setLong(parameterIndex, x)
  def setNull(parameterIndex:Int, sqlType:Int) = self.setNull(parameterIndex, sqlType)
  def setNull(paramIndex:Int, sqlType:Int, typeName:String) = self.setNull(paramIndex, sqlType, typeName)
  def setObject(parameterIndex:Int, x:Object) = self.setObject(parameterIndex, x)
  def setObject(parameterIndex:Int, x:Object, targetSqlType:Int) = self.setObject(parameterIndex, x, targetSqlType)
  def setObject(parameterIndex:Int, x:Object, targetSqlType:Int, scale:Int) = self.setObject(parameterIndex, x, targetSqlType, scale)
  def setRef(i:Int, x:Ref) = self.setRef(i, x)
  def setShort(parameterIndex:Int, x:Short) = self.setShort(parameterIndex, x)
  def setString(parameterIndex:Int, x:String) = self.setString(parameterIndex, x)
  def setTime(parameterIndex:Int, x:Time) = self.setTime(parameterIndex:Int, x)
  def setTime(parameterIndex:Int, x:Time, cal:Calendar) = self.setTime(parameterIndex, x, cal)
  def setTimestamp(parameterIndex:Int, x:Timestamp) = self.setTimestamp(parameterIndex, x)
  def setTimestamp(parameterIndex:Int, x:Timestamp, cal:Calendar) = self.setTimestamp(parameterIndex, x, cal)
  def setUnicodeStream(parameterIndex:Int, x:InputStream, length:Int) = self.setUnicodeStream(parameterIndex, x, length)
  def setURL(parameterIndex:Int, x:URL) = self.setURL(parameterIndex, x)


  def setNClob(x1:Int, x2:Reader) = self.setNClob(x1, x2)
  def setBlob(x1:Int, x2:InputStream) = self.setBlob(x1, x2)
  def setClob(x1:Int, x2:Reader) = self.setClob(x1, x2)
  def setNCharacterStream(x1:Int, x2:Reader) = self.setNCharacterStream(x1, x2)
  def setCharacterStream(x1:Int, x2:Reader) = self.setCharacterStream(x1, x2)
  def setBinaryStream(x1:Int, x2:InputStream) = self.setBinaryStream(x1, x2)
  def setAsciiStream(x1:Int, x2:InputStream) = self.setAsciiStream(x1, x2)
  def setCharacterStream(x1:Int, x2:Reader, x3:Long) = self.setCharacterStream(x1, x2, x3)
  def setBinaryStream(x1:Int, x2:InputStream, x3:Long) = self.setBinaryStream(x1, x2, x3)
  def setAsciiStream(x1:Int, x2:InputStream, x3:Long) = self.setAsciiStream(x1, x2, x3)
  def setSQLXML(x1:Int, x2:SQLXML) = self.setSQLXML(x1, x2)
  def setNClob(x1:Int, x2:Reader, x3:Long) = self.setNClob(x1, x2, x3)
  def setBlob(x1:Int, x2:InputStream, x3:Long) = self.setBlob(x1, x2, x3)
  def setClob(x1:Int, x2:Reader, x3:Long) = self.setClob(x1, x2, x3)
  def setNClob(x1:Int, x2:NClob) = self.setNClob(x1, x2)
  def setNCharacterStream(x1:Int, x2:Reader, x3:Long) = self.setNCharacterStream(x1, x2, x3)
  def setNString(x1:Int, x2:String) = self.setNString(x1, x2)
  def setRowId(x1:Int, x2:RowId) = self.setRowId(x1, x2)
  def setPoolable(x1:Boolean) = self.setPoolable(x1)
  def getMoreResults(x1:Int):Boolean = self.getMoreResults(x1)
  def addBatch(x1:String) = self.addBatch(x1)
  def setFetchSize(x1:Int) = self.setFetchSize(x1)
  def setFetchDirection(x1:Int) = self.setFetchDirection(x1)
  def setCursorName(x1:String) = self.setCursorName(x1)
  def setQueryTimeout(x1:Int) = self.setQueryTimeout(x1)
  def setEscapeProcessing(x1:Boolean) = self.setEscapeProcessing(x1)
  def setMaxRows(x1:Int) = self.setMaxRows(x1)
  def setMaxFieldSize(x1:Int) = self.setMaxFieldSize(x1)

  def isPoolable:Boolean = self.isPoolable
  def getResultSetHoldability:Int = self.getResultSetHoldability
  def getGeneratedKeys:ResultSet = self.getGeneratedKeys
  def getConnection:Connection = self.getConnection
  def executeBatch:scala.Array[Int] = self.executeBatch
  def clearBatch = self.clearBatch
  def getResultSetType:Int = self.getResultSetType
  def getResultSetConcurrency:Int = self.getResultSetConcurrency
  def getFetchSize:Int = self.getFetchSize
  def getFetchDirection:Int = self.getFetchDirection
  def getMoreResults:Boolean = self.getMoreResults
  def getUpdateCount:Int = self.getUpdateCount
  def clearWarnings = self.clearWarnings
  def getWarnings:SQLWarning = self.getWarnings
  def getQueryTimeout:Int = self.getQueryTimeout
  def getMaxRows:Int = self.getMaxRows
  def getMaxFieldSize:Int = self.getMaxFieldSize

  def isWrapperFor(iface:java.lang.Class[_]):Boolean = self.isWrapperFor(iface)
  def unwrap[T](iface:java.lang.Class[T]):T = self.unwrap(iface)

}

/**
 * This is a proxy object that implements the JDBC Connection object interface for the
 * purpose of caching prepared statements and disabling auto-commit, which is not supported
 * in this database pool.
 */
class PooledConnection(val conn:Connection) extends Connection with Proxy with Logging {
  import java.sql._
  import java.util.Properties
  def self:Connection = conn
  def selfId:String = Unique()

  def close = {
    throw new Exception("Pooled connections should not be closed")
  }

  def reallyClose = {
    closeAllTemporaryObjects
    // clean out the cache
    log.debug("%s: Clearing out all cached prepared statements", this.toString)
    pcache1._vals.values.foreach(_.close)
    pcache2._vals.values.foreach(_.close)
    pcache3._vals.values.foreach(_.close)
    pcache4._vals.values.foreach(_.close)
    pcache5._vals.values.foreach(_.close)
    pcache6._vals.values.foreach(_.close)
    self.close
  }

  def closeAllTemporaryObjects = {
    log.trace("%s: Closing any open result sets", this.toString)
    pcache1._vals.values.foreach(_.closeOpenResultSets)
    pcache2._vals.values.foreach(_.closeOpenResultSets)
    pcache3._vals.values.foreach(_.closeOpenResultSets)
    pcache4._vals.values.foreach(_.closeOpenResultSets)
    pcache5._vals.values.foreach(_.closeOpenResultSets)
    pcache6._vals.values.foreach(_.closeOpenResultSets)
  }

  override def toString = "[PooledConnection self=%s, id=%s]".format(self, selfId)

  // Memoize each prepareStatement function
  lazy private val pcache1 = Memoize1 { (sql:String) => new PreparedStatementProxy(self.prepareStatement(sql)) }
  def prepareStatement(sql:String):PreparedStatement = pcache1(sql)

  lazy private val pcache2 = Memoize2 { (sql:String, autoGeneratedKeys:Int) => new PreparedStatementProxy(self.prepareStatement(sql, autoGeneratedKeys)) }
  def prepareStatement(sql:String, autoGeneratedKeys:Int):PreparedStatement = pcache2(sql, autoGeneratedKeys)

  lazy private val pcache3 = Memoize2 { (sql:String, columnIndexes:scala.Array[Int]) => new PreparedStatementProxy(self.prepareStatement(sql, columnIndexes)) }
  def prepareStatement(sql:String, columnIndexes:scala.Array[Int]):PreparedStatement = pcache3(sql, columnIndexes)

  lazy private val pcache4 = Memoize3 { (sql:String, resultSetType:Int, resultSetConcurrency:Int) => new PreparedStatementProxy(self.prepareStatement(sql, resultSetType, resultSetConcurrency)) }
  def prepareStatement(sql:String, resultSetType:Int, resultSetConcurrency:Int):PreparedStatement = pcache4(sql, resultSetType, resultSetConcurrency)

  lazy private val pcache5 = Memoize4 { (sql:String, resultSetType:Int, resultSetConcurrency:Int, resultSetHoldability:Int) => new PreparedStatementProxy(self.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)) }
  def prepareStatement(sql:String, resultSetType:Int, resultSetConcurrency:Int, resultSetHoldability:Int):PreparedStatement = pcache5(sql, resultSetType, resultSetConcurrency, resultSetHoldability)

  lazy private val pcache6 = Memoize2 { (sql:String, columnNames:scala.Array[String]) => new PreparedStatementProxy(self.prepareStatement(sql, columnNames)) }
  def prepareStatement(sql:String, columnNames:scala.Array[String]):PreparedStatement = pcache6(sql, columnNames)

  def setAutoCommit(autoCommit:Boolean):Unit = autoCommit match {
    case true => throw new Exception("Auto-commit is not supported by this database pool")
    case false =>
  }
  def getAutoCommit():Boolean = false

  def commit():Unit = self.commit()
  def clearWarnings():Unit = self.clearWarnings()
  def createArrayOf(typeName:String, elements:scala.Array[Object]):Array = self.createArrayOf(typeName, elements)
  def createBlob():Blob = self.createBlob()
  def createClob():Clob = self.createClob()
  def createNClob():NClob = self.createNClob()
  def createSQLXML():SQLXML = self.createSQLXML()
  def createStatement():Statement = self.createStatement()
  def createStatement(resultSetType:Int, resultSetConcurrency:Int):Statement = self.createStatement(resultSetType, resultSetConcurrency)
  def createStatement(resultSetType:Int, resultSetConcurrency:Int, resultSetHoldability:Int):Statement = self.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)
  def createStruct(typeName:String, attributes:scala.Array[Object]):Struct = self.createStruct(typeName, attributes)
  def getCatalog():String = self.getCatalog()
  def getClientInfo():Properties = self.getClientInfo()
  def getClientInfo(name:String):String = self.getClientInfo(name)
  def getHoldability():Int = self.getHoldability()
  def getMetaData():DatabaseMetaData = self.getMetaData()
  def getTransactionIsolation():Int = self.getTransactionIsolation()
  def getTypeMap():java.util.Map[String,Class[_]] = self.getTypeMap()
  def getWarnings():SQLWarning = self.getWarnings()
  def isClosed():Boolean = self.isClosed()
  def isReadOnly():Boolean = self.isReadOnly()
  def isValid(timeout:Int):Boolean = self.isValid(timeout)
  def nativeSQL(sql:String):String = self.nativeSQL(sql)
  def prepareCall(sql:String):CallableStatement = self.prepareCall(sql)
  def prepareCall(sql:String, resultSetType:Int, resultSetConcurrency:Int):CallableStatement = self.prepareCall(sql, resultSetType, resultSetConcurrency)
  def prepareCall(sql:String, resultSetType:Int, resultSetConcurrency:Int, resultSetHoldability:Int):CallableStatement = self.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability)
  def releaseSavepoint(savepoint:Savepoint):Unit = self.releaseSavepoint(savepoint)
  def rollback():Unit = self.rollback()
  def rollback(savepoint:Savepoint):Unit = self.rollback(savepoint)
  def setCatalog(catalog:String):Unit = self.setCatalog(catalog)
  def setClientInfo(properties:Properties):Unit = self.setClientInfo(properties)
  def setClientInfo(name:String, value:String):Unit = self.setClientInfo(name, value)
  def setHoldability(holdability:Int):Unit = self.setHoldability(holdability)
  def setReadOnly(readOnly:Boolean):Unit = self.setReadOnly(readOnly)
  def setSavepoint():Savepoint = self.setSavepoint()
  def setSavepoint(name:String):Savepoint = self.setSavepoint(name)
  def setTransactionIsolation(level:Int):Unit = self.setTransactionIsolation(level)
  def setTypeMap(map:java.util.Map[String,Class[_]]):Unit = self.setTypeMap(map)
  def isWrapperFor(iface:java.lang.Class[_]):Boolean = self.isWrapperFor(iface)
  def unwrap[T](iface:java.lang.Class[T]):T = self.unwrap(iface)
}

