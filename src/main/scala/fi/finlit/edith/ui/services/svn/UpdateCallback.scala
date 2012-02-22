package fi.finlit.edith.ui.services.svn

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
//remove if not needed
import scala.collection.JavaConversions._

trait UpdateCallback {

  /**
   * @param source
   * @param target
   */
  def update(source: InputStream, target: OutputStream): Unit
}
