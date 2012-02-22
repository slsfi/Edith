package fi.finlit.edith.domain

import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class UrlElement(@BeanProperty val string: String) extends ParagraphElement {

  @BeanProperty
  var url: String = _

  override def copy(): ParagraphElement = {
    var element = new UrlElement(string)
    element.setUrl(url)
    element
  }

  override def toString(): String = {
    "<a" + (if (url == null) "" else " href=\"" + url + "\"") + ">" + string + "</a>"
  }
}
