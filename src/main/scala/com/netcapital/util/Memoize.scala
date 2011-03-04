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

/**
 * Memoizes results to a function passed to the constructor.
 *
 * Usage (cache) is thread-safe.
 */
class Memoize1[T, R](f: T => R) extends (T => R) with Logging {
  val unique = Unique("memoizer")
  val _vals = new mutable.HashMap[T,R] with mutable.SynchronizedMap[T, R]

  def apply(x: T): R = {
    if (_vals.contains(x)) {
      log.debug("%s: using memoized cache for %s", unique,x)
      _vals(x)
    } else {
      val y = f(x)
      _vals += ((x, y))
      log.debug("%s: memoize cache miss, dump: %s", unique, _vals)
      log.debug("%s: memoized %s for %s", unique, y, x)
      y
    }
  }
}

object Memoize1 {
  def apply[T, R](f: T => R) = new Memoize1(f)
}



class Memoize2[T, U, R](f: (T, U) => R) extends ((T, U) => R) with Logging {
  val unique = Unique("memoizer")
  val _vals = new mutable.HashMap[Tuple2[T,U], R] with mutable.SynchronizedMap[Tuple2[T,U], R]

  def apply(x1: T, x2: U): R = {
    if (_vals.contains((x1, x2))) {
      log.debug("%s: using memoized cache for (%s, %s)", unique, x1, x2)
      _vals((x1, x2))
    } else {
      val y = f(x1, x2)
      _vals += (((x1, x2), y))
      log.debug("%s: memoize cache miss, dump: %s", unique, _vals)
      log.debug("%s: memoized %s for (%s, %s)", unique, y, x1, x2)
      y
    }
  }
}

object Memoize2 {
  def apply[T, U, R](f: (T, U) => R) = new Memoize2(f)
}



class Memoize3[T, U, V, R](f: (T, U, V) => R) extends ((T, U, V) => R) with Logging {
  val unique = Unique("memoizer")
  val _vals = new mutable.HashMap[Tuple3[T,U,V], R] with mutable.SynchronizedMap[Tuple3[T,U,V], R]

  def apply(x1: T, x2: U, x3: V): R = {
    if (_vals.contains((x1, x2, x3))) {
      log.debug("%s: using memoized cache for (%s, %s, %s)", unique, x1, x2, x3)
      _vals((x1, x2, x3))
    } else {
      val y = f(x1, x2, x3)
      _vals += (((x1, x2, x3), y))
      log.debug("%s: memoize cache miss, dump: %s", unique, _vals)
      log.debug("%s: memoized %s for (%s, %s, %s)", unique, y, x1, x2, x3)
      y
    }
  }
}

object Memoize3 {
  def apply[T, U, V, R](f: (T, U, V) => R) = new Memoize3(f)
}



class Memoize4[T, U, V, X, R](f: (T, U, V, X) => R) extends ((T, U, V, X) => R) with Logging {
  val unique = Unique("memoizer")
  val _vals = new mutable.HashMap[Tuple4[T,U,V,X], R] with mutable.SynchronizedMap[Tuple4[T,U,V,X], R]

  def apply(x1: T, x2: U, x3: V, x4: X): R = {
    if (_vals.contains((x1, x2, x3, x4))) {
      log.debug("%s: using memoized cache for (%s,%s,%s,%s)", unique, x1, x2, x3, x4)
      _vals((x1, x2, x3, x4))
    } else {
      val y = f(x1, x2, x3, x4)
      _vals += (((x1, x2, x3, x4), y))
      log.debug("%s: memoize cache miss, dump: %s", unique, _vals)
      log.debug("%s: memoized %s for (%s, %s, %s, %s)", unique, y, x1, x2, x3, x4)
      y
    }
  }
}

object Memoize4 {
  def apply[T, U, V, X, R](f: (T, U, V, X) => R) = new Memoize4(f)
}


