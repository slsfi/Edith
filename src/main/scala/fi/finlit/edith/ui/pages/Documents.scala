package fi.finlit.edith.ui.pages

import java.util.Collection
import java.util.HashSet
import java.util.List
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.annotations.RequestParameter
import org.apache.tapestry5.annotations.SessionState
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.PageRenderLinkSource
import org.apache.tapestry5.util.TextStreamResponse
import com.google.gson.Gson
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.ui.services.svn.FileItemWithDocumentId
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("classpath:js/jquery-1.5.1.min.js", "classpath:js/jquery-ui-1.8.12.custom.min.js", "classpath:js/jquery.cookie.js", "deleteDialog.js", "classpath:js/jquery.dynatree.js", "classpath:js/url-encoder.js"), stylesheet = ("context:styles/dynatree/skin/ui.dynatree.css"))
class Documents {

  @Inject
  private var documentDao: DocumentDao = _

  @Property
  private var document: Document = _

  @SessionState(create = false)
  private var selectedDocuments: Collection[Document] = _

  private var selectedForDeletion: Collection[Document] = _

  private var toBeDeleted: Collection[Document] = _

  private var removeSelected: Boolean = _

  @Inject
  private var linkSource: PageRenderLinkSource = _

  def onActivate() {
    selectedForDeletion = new HashSet[Document]()
    if (selectedDocuments == null) {
      selectedDocuments = new HashSet[Document]()
    }
  }

  def isDocumentSelected(): Boolean = selectedDocuments.contains(document)

  def setDocumentSelected(selected: Boolean) {
    if (selected) {
      selectedDocuments.add(document)
    } else {
      selectedDocuments.remove(document)
    }
  }

  def onSelectedFromRemoveSelected() {
    removeSelected = true
  }

  def onSuccessFromDocumentsForm() {
    if (removeSelected) {
      documentDao.removeAll(selectedForDeletion)
    }
  }

  def isSelectedForDeletion(): Boolean = selectedForDeletion.contains(document)

  def setSelectedForDeletion(selected: Boolean) {
    if (selected) {
      selectedForDeletion.add(document)
    } else {
      selectedForDeletion.remove(document)
    }
  }

  def onJson(@RequestParameter(value = "path", allowBlank = true) path: String, @RequestParameter(value = "id", allowBlank = true) id: java.lang.Long): TextStreamResponse = {
    var gson = new Gson()
    var fileItems = documentDao.fromPath(path, id)
    new TextStreamResponse("application/json", gson.toJson(fileItems))
  }

  def getDocumentsAjaxURL(): String = {
    linkSource.createPageRenderLink(classOf[Documents]).toString
  }

  def getAnnotateURL(): String = {
    linkSource.createPageRenderLinkWithContext(classOf[Annotate]).toString
  }

  def getDeleteDocumentURL(): String = {
    linkSource.createPageRenderLink(classOf[Documents]).toString + ".deletedocument"
  }

  def getRenameDocumentURL(): String = {
    linkSource.createPageRenderLink(classOf[Documents]).toString + ".renamedocument"
  }

  def onActionFromDeleteDocument(id: java.lang.Long) {
    documentDao.remove(id)
  }

  def onActionFromRenameDocument(id: java.lang.Long, newName: String) {
    documentDao.rename(id, newName)
  }
}
