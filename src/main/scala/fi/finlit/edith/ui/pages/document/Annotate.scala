package fi.finlit.edith.ui.pages.document

import java.util.List
import org.apache.tapestry5.Block
import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.EventContext
import org.apache.tapestry5.ajax.MultiZoneUpdate
import org.apache.tapestry5.annotations.AfterRender
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.Persist
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.apache.tapestry5.services.PageRenderLinkSource
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import fi.finlit.edith.EDITH
import fi.finlit.edith.dto.NoteSearchInfo
import fi.finlit.edith.dto.SelectedText
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.NoteType
import fi.finlit.edith.sql.domain.Term
import fi.finlit.edith.ui.components.InfoMessage
import fi.finlit.edith.ui.components.note.DocumentNotes
import fi.finlit.edith.ui.components.note.NoteEdit
import fi.finlit.edith.ui.components.note.SearchResults
import fi.finlit.edith.ui.pages.Documents
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.NoteDao
import Annotate._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object Annotate {

  private val EDIT_ZONE = "editZone"

  private val logger = LoggerFactory.getLogger(classOf[Annotate])
}

@Import(library = ("classpath:js/jquery-1.5.1.min.js", "classpath:js/TapestryExt.js", "classpath:js/jquery-ui-1.8.12.custom.min.js", "classpath:js/jquery.dynatree.min.js", "TextSelector.js", "Annotate.js", "classpath:js/jqModal.js", "classpath:js/jquery.cookie.js", "context:js/ckeditor/ckeditor.js", "context:js/ckeditor/adapters/jquery.js"), stylesheet = ("context:styles/tei.css", "context:styles/smoothness/jquery-ui-1.8.12.custom.css", "context:styles/dynatree/skin/ui.dynatree.css", "Annotate.css"))
class Annotate extends AbstractDocumentPage {

  @Property
  @Persist
  private var createTermSelection: SelectedText = _

  @Inject
  private var documentNoteRepository: DocumentNoteDao = _

  @Inject
  @BeanProperty
  var documentView: Block = _

  @Inject
  private var emptyBlock: Block = _

  @Inject
  @Property
  private var closeDialog: Block = _

  @InjectComponent
  @BeanProperty
  var infoMessage: InfoMessage = _

  @Inject
  private var messages: Messages = _

  @Property
  private var moreThanOneSelectable: Boolean = _

  @Property
  private var note: DocumentNote = _

  @Inject
  private var noteRepository: NoteDao = _

  @Property
  private var noteRevisionId: String = _

  @Property
  private var selectedNoteId: String = _

  @Inject
  @Property
  private var notesForLemma: Block = _

  @Inject
  private var renderSupport: JavaScriptSupport = _

  @Inject
  private var resources: ComponentResources = _

  @Persist
  private var searchInfo: NoteSearchInfo = _

  @Property
  private var `type`: NoteType = _

  @Property
  private var notes: List[Note] = _

  @Property
  private var personId: String = _

  @Property
  private var noteToLinkId: java.lang.Long = _

  @Inject
  @Symbol(EDITH.EXTENDED_TERM)
  @BeanProperty
  var slsMode: Boolean = _

  @InjectComponent
  @BeanProperty
  var searchResults: SearchResults = _

  @InjectComponent
  @BeanProperty
  var documentNotes: DocumentNotes = _

  @InjectComponent
  @BeanProperty
  var noteEdit: NoteEdit = _

  @Inject
  private var linkSource: PageRenderLinkSource = _

  @Inject
  @Property
  private var personForm: Block = _

  @Inject
  @Property
  private var placeForm: Block = _

  @Inject
  @Property
  private var termForm: Block = _

  @AfterRender
  def addScript() {
    var link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI()
    renderSupport.addScript("editLink = '" + link + "';")
  }

  def getSearchInfo(): NoteSearchInfo = {
    if (searchInfo == null) {
      searchInfo = new NoteSearchInfo()
      searchInfo.getDocuments.add(getDocument)
      searchInfo.setCurrentDocument(getDocument)
    }
    searchInfo
  }

  def setupRender() {
    searchInfo = null
  }

  def onActivate() {
    if (createTermSelection == null) {
      createTermSelection = new SelectedText()
    }
  }

  def onSuccessFromSelectNoteForm(): AnyRef = {
    var firstSlash = selectedNoteId.indexOf('/')
    var id = Long.parseLong(if (firstSlash == -1) selectedNoteId.substring(1) else selectedNoteId.substring(1, firstSlash))
    var documentNote = documentNoteRepository.getById(id)
    documentNotes.setNoteId(documentNote.getNote.getId)
    documentNotes.setSelectedNote(documentNote)
    noteEdit.setDocumentNoteOnEdit(documentNote)
    new MultiZoneUpdate("documentNotesZone", documentNotes.getBlock).add("noteEditZone", noteEdit.getBlock)
  }

  def onDelete(context: EventContext): AnyRef = {
    new MultiZoneUpdate(EDIT_ZONE, emptyBlock).add("documentZone", documentView).add("commentZone", emptyBlock)
  }

  def onEdit(context: EventContext): AnyRef = {
    new MultiZoneUpdate(EDIT_ZONE, noteEdit)
  }

  private def noteHasChanged(documentNote: DocumentNote, msg: String): MultiZoneUpdate = {
    documentNotes.setNoteId(documentNote.getNote.getId)
    documentNotes.setSelectedNote(documentNote)
    noteEdit.setDocumentNoteOnEdit(documentNote)
    infoMessage.addInfoMsg(msg)
    zoneWithInfo(msg).add("listZone", searchResults.getBlock).add("documentNotesZone", documentNotes.getBlock).add("noteEditZone", noteEdit.getBlock).add("documentZone", documentView)
  }

  def onSuccessFromConnectTermForm(): AnyRef = {
    logger.info("connect term with note id " + noteToLinkId)
    try {
      var n = noteRepository.getById(noteToLinkId)
      var documentNote = noteRepository.createDocumentNote(n, getDocument, createTermSelection.getSelection)
      documentNote = getDocumentDao.updateNote(documentNote, createTermSelection)
      noteHasChanged(documentNote, "note-connect-success")
    } catch {
      case e: Exception => zoneWithError("note-connect-failed", e)
    }
  }

  def onSuccessFromCreateTermForm(): AnyRef = {
    logger.info(createTermSelection.toString)
    var documentNote: DocumentNote = null
    try {
      var n = createNote()
      documentNote = getDocumentDao.addNote(n, getDocument, createTermSelection)
    } catch {
      case e: Exception => return zoneWithError("note-addition-failed", e)
    }
    noteHasChanged(documentNote, "create-success")
  }

  private def createNote(): Note = {
    var n = new Note()
    if (slsMode) {
      n.setTerm(new Term())
    }
    n
  }

  def setSearchInfo(searchInfo: NoteSearchInfo) {
    this.searchInfo = searchInfo
  }

  def onChooseBackingNote(): AnyRef = handleUserChoice(null)

  def onChooseBackingNote(noteId: String): AnyRef = handleUserChoice(noteId)

  private def handleUserChoice(noteId: String): AnyRef = {
    new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("dialogZone", closeDialog)
  }

  def zoneWithInfo(msg: String): MultiZoneUpdate = {
    getInfoMessage.addInfoMsg(msg)
    new MultiZoneUpdate("infoMessageZone", getInfoMessage.getBlock)
  }

  def zoneWithError(msg: String, e: Throwable): MultiZoneUpdate = {
    logger.error(msg, e)
    getInfoMessage.addErrorMsg(msg)
    new MultiZoneUpdate("infoMessageZone", getInfoMessage.getBlock)
  }

  def getDocumentsAjaxURL(): String = {
    linkSource.createPageRenderLink(classOf[Documents]).toString
  }

  def onCreatePerson(): AnyRef = personForm

  def onCreatePlace(): AnyRef = placeForm

  def onCreateTerm(): AnyRef = termForm
}
