package fi.finlit.edith.sql.domain

import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class LinkElement(@BeanProperty val string: String) extends ParagraphElement {

  @BeanProperty
  var reference: String = _

  override def copy(): ParagraphElement = {
    var element = new LinkElement(string)
    element.setReference(reference)
    element
  }

  override def toString(): String = {
    "<bibliograph" + (if (reference == null) "" else " ref=\"" + reference + "\"") + ">" + string + "</bibliograph>"
  }
}
