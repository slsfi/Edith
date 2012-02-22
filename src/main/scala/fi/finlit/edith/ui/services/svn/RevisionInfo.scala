package fi.finlit.edith.ui.services.svn

import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class RevisionInfo(@BeanProperty val svnRevision: Long, @BeanProperty val created: String, @BeanProperty val creator: String) {

  def this(svnRevision: Long) {
    this.svnRevision = svnRevision
    this.created = ""
    this.creator = ""
  }

  override def hashCode(): Int = Long.valueOf(svnRevision).hashCode

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
    var other = obj.asInstanceOf[RevisionInfo]
    if (svnRevision != other.svnRevision) {
      return false
    }
    true
  }
}
