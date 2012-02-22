package fi.finlit.edith.ui.services.svn

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(2137588590021188211L)
class SubversionException() extends RuntimeException {

  def this(t: Throwable) {
    super(t)
  }

  def this(msg: String, t: Throwable) {
    super(msg, t)
  }
}
