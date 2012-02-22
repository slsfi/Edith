package fi.finlit.edith.sql.domain

import java.util.ArrayList
import java.util.List
import org.apache.commons.lang.StringUtils
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class Paragraph {

  @BeanProperty
  val elements = new ArrayList[ParagraphElement]()

  def addElement(e: ParagraphElement): Paragraph = {
    elements.add(e)
    this
  }

  override def hashCode(): Int = toString.hashCode

  override def equals(obj: Any): Boolean = {
    if (obj == this) {
      true
    } else if (obj.isInstanceOf[Paragraph]) {
      obj.toString == toString
    } else {
      false
    }
  }

  override def toString(): String = StringUtils.join(elements, "")
}
