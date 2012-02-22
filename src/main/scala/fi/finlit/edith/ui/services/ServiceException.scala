package fi.finlit.edith.ui.services

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(-5426150520106835552L)
class ServiceException(msg: String) extends RuntimeException(msg) {

  def this(t: Throwable) {
    super(t)
  }

  def this(msg: String, t: Throwable) {
    super(msg, t)
  }
}
