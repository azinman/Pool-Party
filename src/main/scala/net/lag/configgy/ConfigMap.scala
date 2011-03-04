/*
 * Copyright 2009 Robey Pointer <robeypointer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lag.configgy

import scala.collection.Map
import scala.util.Sorting
import net.lag.logging.Logger

class ConfigException(reason: String) extends Exception(reason)

object ConfigMap {
  final val TRUE = true.toString
  final val FALSE = false.toString
  
  def numWrap[A](f: => A): Option[A] =
    try Some(f) catch { case _: NumberFormatException => None }    
}
import ConfigMap._

/**
 * Abstract trait for a map of string keys to strings, string lists, or (nested) ConfigMaps.
 * Integers and booleans may also be stored and retrieved, but they are converted to/from
 * strings in the process.
 */
trait ConfigMap {
  
  // -----  required methods

  /**
   * Lookup an entry in this map, and if it exists and can be represented
   * as a string, return it. Strings will be returned as-is, and string
   * lists will be returned as a combined string. Nested AttributeMaps
   * will return None as if there was no entry present.
   */
  def getString(key: String): Option[String]

  /**
   * Lookup an entry in this map, and if it exists and is a nested
   * ConfigMap, return it. If the entry is a string or string list,
   * it will return None as if there was no entry present.
   */
  def getConfigMap(key: String): Option[ConfigMap]

  /**
   * Lookup an entry in this map, and if it exists and is a nested
   * ConfigMap, return it. If not, create an empty map with this name
   * and return that.
   *
   * @throws ConfigException if the key already refers to a string or
   *     string list
   */
  def configMap(key: String): ConfigMap

  /**
   * Lookup an entry in this map, and if it exists and can be represented
   * as a string list, return it. String lists will be returned as-is, and
   * strings will be returned as an array of length 1. If the entry doesn't
   * exist or is a nested ConfigMap, an empty sequence is returned.
   */
  def getList(key: String): Seq[String]

  /**
   * Set a key/value pair in this map. If an entry already existed with
   * that key, it's replaced.
   *
   * @throws ConfigException if the key already refers to a nested
   *     ConfigMap
   */
  def setString(key: String, value: String): Unit

  /**
   * Set a key/value pair in this map. If an entry already existed with
   * that key, it's replaced.
   *
   * @throws ConfigException if the key already refers to a nested
   *     ConfigMap
   */
  def setList(key: String, value: Seq[String]): Unit

  /**
   * Put a nested ConfigMap inside this one. If an entry already existed with
   * that key, it's replaced. The ConfigMap is deep-copied at insert-time.
   *
   * @throws ConfigException if the key already refers to a value that isn't
   *     a nested ConfigMap
   */
  def setConfigMap(key: String, value: ConfigMap): Unit

  /**
   * Returns true if this map contains the given key.
   */
  def contains(key: String): Boolean

  /**
   * Remove an entry with the given key, if it exists. Returns true if
   * an entry was actually removed, false if not.
   */
  def remove(key: String): Boolean

  /**
   * Return an iterator across the keys of this map.
   */
  def keys: Iterator[String]

  /**
   * Return a new (immutable) map containing a deep copy of all the keys
   * and values from this AttributeMap. Keys from nested maps will be
   * compound (like `"inner.name"`).
   */
  def asMap(): Map[String, String]

  /**
   * Subscribe to changes on this map. Any changes (including deletions)
   * that occur on this node will be sent through the subscriber to
   * validate and possibly commit. See {@link Subscriber} for details
   * on the validate/commit process.
   *
   * @return a key which can be used to cancel the subscription
   */
  def subscribe(subscriber: Subscriber): SubscriptionKey

  /**
   * Make a deep copy of this ConfigMap. Any inheritance chains will be
   * deep-copied, but the inheritance will not be preserved: the copied
   * ConfigMap stands alone as its own set of objects, reflecting the
   * frozen state of any inherited ConfigMaps.
   */
  def copy(): ConfigMap

  /**
   * Make this ConfigMap inherit default values from another ConfigMap.
   * Any attributes that aren't explicitly set will fall back to the inherited
   * ConfigMap on lookup.
   */
  def inheritFrom_=(config: Option[ConfigMap]): Unit

  /**
   * Return any ConfigMap that is used as a fall back on lookups.
   */
  def inheritFrom: Option[ConfigMap]


  // -----  convenience methods

  /**
   * If the requested key is present, return its value. Otherwise, return
   * the given default value.
   */
  def getString(key: String, defaultValue: String): String =
    getString(key) getOrElse defaultValue

  /**
   * If the requested key is present and can be converted into an int
   * (via `String.toInt`), return that int. Otherwise, return `None`.
   */
  def getInt(key: String): Option[Int] =
    getString(key) flatMap (x => numWrap(x.toInt))

  /**
   * If the requested key is present and can be converted into an int
   * (via `String.toInt`), return that int. Otherwise,
   * return the given default value.
   */
  def getInt(key: String, defaultValue: Int): Int =
    getInt(key) getOrElse defaultValue

  /**
   * If the requested key is present and can be converted into a long
   * (via `String.toLong`), return that long. Otherwise, return `None`.
   */
  def getLong(key: String): Option[Long] =
    getString(key) flatMap (x => numWrap(x.toLong))

  /**
   * If the requested key is present and can be converted into a long
   * (via `String.toLong`), return that long. Otherwise,
   * return the given default value.
   */
  def getLong(key: String, defaultValue: Long): Long =
    getLong(key) getOrElse defaultValue

  /**
   * If the requested key is present and can be converted into a double
   * (via `String.toDouble`), return that double. Otherwise, return `None`.
   */
  def getDouble(key: String): Option[Double] =
    getString(key) flatMap (x => numWrap(x.toDouble))

  /**
   * If the requested key is present and can be converted into a double
   * (via `String.toDouble`), return that double. Otherwise,
   * return the given default value.
   */
  def getDouble(key: String, defaultValue: Double): Double =
    getDouble(key) getOrElse defaultValue

  /**
   * If the requested key is present and can be converted into a bool
   * (by being either `"true"` or `"false"`),
   * return that bool. Otherwise, return `None`.
   */
  def getBool(key: String): Option[Boolean] = getString(key) map { 
    case TRUE   => true
    case FALSE  => false
    case _      => throw new ConfigException("invalid boolean value")
  }

  /**
   * If the requested key is present and can be converted into a bool
   * (by being either `"true"` or `"false"`),
   * return that bool. Otherwise, return the given default value.
   */
  def getBool(key: String, defaultValue: Boolean): Boolean =
    getBool(key) getOrElse defaultValue

  /**
   * Set the given key to an int value, by converting it to a string
   * first.
   */
  def setInt(key: String, value: Int): Unit = setString(key, value.toString)

  /**
   * Set the given key to a long value, by converting it to a string
   * first.
   */
  def setLong(key: String, value: Long): Unit = setString(key, value.toString)

  /**
   * Set the given key to a double value, by converting it to a string
   * first.
   */
  def setDouble(key: String, value: Double): Unit = setString(key, value.toString)

  /**
   * Set the given key to a bool value, by converting it to a string
   * first.
   */
  def setBool(key: String, value: Boolean): Unit = setString(key, if (value) TRUE else FALSE)

  /**
   * Return the keys of this map, in sorted order.
   */
  def sortedKeys() = keys.toList sortWith (_ < _)

  /**
   * Subscribe to changes on this ConfigMap, but don't bother with
   * validating. Whenever this ConfigMap changes, a new copy will be
   * passed to the given function.
   */
  def subscribe(f: (Option[ConfigMap]) => Unit): SubscriptionKey =
    subscribe(new Subscriber {
      def validate(current: Option[ConfigMap], replacement: Option[ConfigMap]) { }
      def commit(current: Option[ConfigMap], replacement: Option[ConfigMap]) { f(replacement) }
    })

  /**
   * Convert this ConfigMap into a string which could be written into a config
   * file and parsed by configgy.
   */
  def toConfigString: String

  def copyInto(obj: AnyRef) {
    val cls = obj.getClass
    val log = Logger.get(cls)
    val methods = cls.getMethods().filter { method =>
      method.getName().endsWith("_$eq") && method.getParameterTypes().size == 1
    }.toList
    keys.foreach { key =>
      val setters = methods.filter { _.getName() == key + "_$eq" }
      if (setters.size == 0) {
        log.warning("Ignoring config key '%s' which doesn't have a setter in class %s", key, cls)
      }
      setters.foreach { method =>
        val expectedType = method.getParameterTypes().head.getCanonicalName
        val param = expectedType match {
          case "int" => getInt(key)
          case "long" => getLong(key)
          case "float" => getDouble(key).map { _.toFloat }
          case "double" => getDouble(key)
          case "boolean" => getBool(key)
          case "java.lang.String" => getString(key)
          case _ => None // ignore for now
        }
        param.map { p => method.invoke(obj, p.asInstanceOf[Object]) }
      }
    }
  }

  /**
   * If the requested key is present, return its value as a string. Otherwise, throw a
   * ConfigException. `toInt` and `toBoolean` may be called on the
   * returned string if an int or bool is desired.
   */
  def apply(key: String): String =
    getString(key) getOrElse (throw new ConfigException("undefined config: " + key))

  /** Equivalent to `getString(key, defaultValue)`. */
  def apply(key: String, defaultValue: String) = getString(key, defaultValue)

  /** Equivalent to `getInt(key, defaultValue)`. */
  def apply(key: String, defaultValue: Int) = getInt(key, defaultValue)

  /** Equivalent to `getLong(key, defaultValue)`. */
  def apply(key: String, defaultValue: Long) = getLong(key, defaultValue)

  /** Equivalent to `getBool(key, defaultValue)`. */
  def apply(key: String, defaultValue: Boolean) = getBool(key, defaultValue)

  /** Equivalent to `setString(key, value)`. */
  def update(key: String, value: String) = setString(key, value)

  /** Equivalent to `setInt(key, value)`. */
  def update(key: String, value: Int) = setInt(key, value)

  /** Equivalent to `setLong(key, value)`. */
  def update(key: String, value: Long) = setLong(key, value)

  /** Equivalent to `setBool(key, value)`. */
  def update(key: String, value: Boolean) = setBool(key, value)

  /** Equivalent to `setList(key, value)`. */
  def update(key: String, value: Seq[String]) = setList(key, value)
}
