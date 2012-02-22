package fi.finlit.edith.domain

//remove if not needed
import scala.collection.JavaConversions._

class StringElement(string: String) extends ParagraphElement {

  override def copy(): ParagraphElement = new StringElement(string)

  override def toString(): String = string
}
