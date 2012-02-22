package fi.finlit.edith.ui.services.svn

import java.util.List
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class FileItem(@BeanProperty val title: String, 
    @BeanProperty val path: String, 
    isFolder: Boolean, 
    @BeanProperty val children: List[FileItem], 
    hasChildren: Boolean) {

  private val isLazy = isFolder

  def isFolder(): Boolean = isFolder

  def isLazy(): Boolean = isLazy

  def hasChildren(): Boolean = hasChildren
}
