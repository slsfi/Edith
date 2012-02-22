package fi.finlit.edith.sql.domain

import javax.persistence.Embeddable
import org.apache.commons.lang.StringUtils
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

@Embeddable
class NameForm() {

  @BeanProperty
  var description: String = _

  @BeanProperty
  var first: String = _

  @BeanProperty
  var last: String = _

  def this(name: String, description: String) {
    last = name
    this.description = description
  }

  def this(first: String, last: String, description: String) {
    this.first = first
    this.last = last
    this.description = description
  }

  def getName(): String = {
    var builder = new StringBuilder()
    if (first != null) {
      builder.append(first)
    }
    if (first != null && last != null) {
      builder.append(" ")
    }
    if (last != null) {
      builder.append(last)
    }
    builder.toString
  }

  def isValid(): Boolean = {
    StringUtils.isNotBlank(first) || StringUtils.isNotBlank(last)
  }

  override def toString(): String = {
    "NameForm [description=" + description + ", name=" + getName + "]"
  }
}
