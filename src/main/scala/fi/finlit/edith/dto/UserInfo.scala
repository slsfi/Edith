package fi.finlit.edith.dto

import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class UserInfo() {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var username: String = _

  def this(id: Long, username: String) {
    this(username)
    this.id = id
  }

  def this(username: String) {
    this.username = username
  }

  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (username == null) 0 else username.hashCode)
    result
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (obj == null) {
      return false
    }
    if (getClass != obj.getClass) {
      return false
    }
    var other = obj.asInstanceOf[UserInfo]
    if (username == null) {
      if (other.username != null) {
        return false
      }
    } else if (username != other.username) {
      return false
    }
    true
  }
}
