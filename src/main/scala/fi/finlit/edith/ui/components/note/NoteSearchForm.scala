package fi.finlit.edith.ui.components.note

import java.util.ArrayList
import java.util.Collection
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import fi.finlit.edith.EDITH
import fi.finlit.edith.dto.NoteSearchInfo
import fi.finlit.edith.dto.OrderBy
import fi.finlit.edith.dto.UserInfo
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.NoteFormat
import fi.finlit.edith.sql.domain.NoteType
import fi.finlit.edith.sql.domain.TermLanguage
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.UserDao
//remove if not needed
import scala.collection.JavaConversions._

class NoteSearchForm {

  @InjectPage
  private var page: Annotate = _

  private val selectedDocuments = new ArrayList[Document]()

  @Property
  private var `type`: NoteType = _

  @Property
  private var format: NoteFormat = _

  @Property
  private var user: UserInfo = _

  @Inject
  private var userDao: UserDao = _

  @Property
  private var loopedOrderBy: OrderBy = _

  @Inject
  @Symbol(EDITH.EXTENDED_TERM)
  private var slsMode: Boolean = _

  def getSelectedDocumentCount(): Int = {
    if (selectedDocuments != null) selectedDocuments.size else 0
  }

  def onSuccessFromNoteSearchForm(): AnyRef = page.getSearchResults.getBlock

  def getUsers(): Collection[UserInfo] = userDao.getAllUserInfos

  def getTypes(): Array[NoteType] = NoteType.values()

  def getFormats(): Array[NoteFormat] = NoteFormat.values()

  def isTypeSelected(): Boolean = {
    page.getSearchInfo.getNoteTypes.contains(`type`)
  }

  def setTypeSelected(selected: Boolean) {
    if (selected) {
      page.getSearchInfo.getNoteTypes.add(`type`)
    } else {
      getSearchInfo.getNoteTypes.remove(`type`)
    }
  }

  def isFormatSelected(): Boolean = {
    getSearchInfo.getNoteFormats.contains(format)
  }

  def setFormatSelected(selected: Boolean) {
    if (selected) {
      getSearchInfo.getNoteFormats.add(format)
    } else {
      getSearchInfo.getNoteFormats.remove(format)
    }
  }

  def isUserSelected(): Boolean = {
    getSearchInfo.getCreators.contains(user)
  }

  def setUserSelected(selected: Boolean) {
    if (selected) {
      getSearchInfo.getCreators.add(user)
    } else {
      getSearchInfo.getCreators.remove(user)
    }
  }

  def setOrderBy(orderBy: OrderBy) {
    getSearchInfo.setOrderBy(orderBy)
  }

  def getOrderBy(): OrderBy = {
    if (getSearchInfo.getOrderBy == null) {
      getSearchInfo.setOrderBy(if (slsMode) OrderBy.KEYTERM else OrderBy.LEMMA)
    }
    getSearchInfo.getOrderBy
  }

  def getOrderBys(): Array[OrderBy] = {
    if (slsMode) {
      return Array[OrderBy](OrderBy.KEYTERM, OrderBy.USER, OrderBy.STATUS, OrderBy.DATE)
    }
    Array[OrderBy](OrderBy.LEMMA, OrderBy.USER, OrderBy.STATUS, OrderBy.DATE)
  }

  def isReversed(): Boolean = !getSearchInfo.isAscending

  def setReversed(reversed: Boolean) {
    getSearchInfo.setAscending(!reversed)
  }

  def isOrphans(): Boolean = getSearchInfo.isOrphans

  def setOrphans(orphans: Boolean) {
    getSearchInfo.setOrphans(orphans)
  }

  def getLanguage(): TermLanguage = getSearchInfo.getLanguage

  def setLanguage(lang: TermLanguage) {
    getSearchInfo.setLanguage(lang)
  }

  def getSearchInfo(): NoteSearchInfo = page.getSearchInfo

  def getPaths(): String = {
    StringUtils.join(getSearchInfo.getPaths, ",")
  }

  def setPaths(paths: String) {
    getSearchInfo.getPaths.clear()
    if (paths == null) return
    for (path <- paths.split(",")) {
      getSearchInfo.getPaths.add(path)
    }
  }

  def getDocuments(): String = {
    var documentIds = new ArrayList[Long]()
    for (document <- getSearchInfo.getDocuments) {
      documentIds.add(document.getId)
    }
    StringUtils.join(documentIds, ",")
  }

  def setDocuments(documents: String) {
    getSearchInfo.getDocuments.clear()
    if (documents == null) return
    for (documentId <- documents.split(",")) {
      var document = new Document()
      document.setId(Long.valueOf(documentId))
      getSearchInfo.getDocuments.add(document)
    }
  }

  def getFullText(): String = getSearchInfo.getFullText

  def setFullText(fullText: String) {
    getSearchInfo.setFullText(fullText)
  }

  def getIncludeAllDocs(): Boolean = getSearchInfo.isIncludeAllDocs

  def setIncludeAllDocs(value: Boolean) {
    getSearchInfo.setIncludeAllDocs(value)
  }
}
