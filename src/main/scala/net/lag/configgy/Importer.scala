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

import java.io.{ BufferedReader, File, FileInputStream, InputStream, InputStreamReader }


/**
 * An interface for finding config files and reading them into strings for
 * parsing. This is used to handle `include` directives in config files.
 */
trait Importer {
  /**
   * Imports a requested file and returns the string contents of that file.
   * If the file couldn't be imported, throws a `ParseException`.
   */
  @throws(classOf[ParseException])
  def importFile(filename: String): String
  
  protected def fail(msg: Any): Nothing = throw new ParseException(msg.toString)

  /**
   * Exhaustively reads an InputStream and converts it into a String (using
   * UTF-8 encoding). This is meant as a helper function for custom Importer
   * classes.
   *
   * No exceptions are caught!
   */
  protected def streamToString(in: InputStream): String = {
    val BUFFER_SIZE = 8192
    val reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))
    val buffer = new Array[Char](BUFFER_SIZE)
    val out = new StringBuilder
    var n = 0
    while (n >= 0) {
      n = reader.read(buffer, 0, buffer.length)
      if (n >= 0) {
        out.appendAll(buffer, 0, n)
      }
    }
    out.toString
  }
}


/**
 * An Importer that looks for imported config files in the filesystem.
 * This is the default importer.
 */
class FilesystemImporter(val baseFolder: String) extends Importer {
  private def toAbsolute(name: String) = {
    val f = new File(name)
    if (f.isAbsolute) f
    else new File(baseFolder, name)
  }
  
  def importFile(filename: String): String =
    try streamToString(new FileInputStream(toAbsolute(filename)))
    catch { case x => fail(x) }
}


/**
 * An Importer that looks for imported config files in the java resources
 * of the system class loader (usually the jar used to launch this app).
 */
class ResourceImporter(classLoader: ClassLoader) extends Importer {
  def importFile(filename: String) =
    try (classLoader.getResourceAsStream(filename) match {
      case null   => fail("Can't find resource: " + filename)
      case stream => streamToString(stream)
    })
    catch { case x => fail(x) }
}
