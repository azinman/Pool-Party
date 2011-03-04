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

package com.netcapital.util

import scala.collection.mutable
import java.util.concurrent._
import java.util.concurrent.locks._
import java.util.Map.Entry
import java.util._

class LRU[K, V](cacheSize: Int) {
  private val cache = new LinkedHashMap[K, V]() {
    override def removeEldestEntry(eldest: Entry[K, V]): Boolean = return size() > cacheSize
  }

  val rwl = new ReentrantReadWriteLock()
  val readLock = rwl.readLock
  val writeLock = rwl.writeLock

  private def lock[T](l: Lock)(block: => T): T = {
    l.lock
    try { block }
    finally { l.unlock }
  }

  /**
   * Returns the cached value mapped with this key or Empty if not found
   *
   * @param key
   * @return Option[V]
   */
  def apply(key: K): Option[V] = lock(readLock) {
    cache.get(key) match {
      case null => None
      case value:V => Some(value)
    }
  }

  def multiget(keys: Seq[K]):mutable.Map[K, V] = {
    val results = mutable.Map[K, V]()
    lock(readLock) {
      for (key <- keys) {
        cache.get(key) match {
          case null =>
          case value:V => results += ((key, value))
        }
      }
    }
    results
  }

  /**
   * Puts a new keyed entry in cache
   * @param tuple: (K, V)*
   * @return this
   */
  def += (tuple: (K, V)*) = {
    lock(writeLock) {
      for (t <- tuple) yield {
        cache.put(t._1, t._2)
      }
    }
    this
  }

  /**
   * Removes the cache entry mapped with this key
   *
   * @returns the value removed
   */
  def remove(key: Any): Option[V] = {
    lock(writeLock) {
      cache.remove(key) match {
        case null => None
        case value:V => Some(value)
      }
    }
  }

  def keys = cache.keySet
}

