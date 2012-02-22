package fi.finlit.edith.ui.services.svn

import java.util.List
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class FileItemWithDocumentId(title: String, 
    path: String, 
    isFolder: Boolean, 
    children: List[FileItem], 
    hasChildren: Boolean, 
    @BeanProperty val documentId: java.lang.Long, 
    isSelected: Boolean, 
    @BeanProperty val noteCount: Long) extends FileItem(title, path, isFolder, children, hasChildren) {



  def isSelected(): Boolean = isSelected
}
