package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QDocumentNote.documentNote
import fi.finlit.edith.sql.domain.QNote.note
import fi.finlit.edith.sql.domain.QPerson.person
import fi.finlit.edith.sql.domain.QPlace.place
import fi.finlit.edith.sql.domain.QTerm.term
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.List
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamReader
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.hibernate.Session
import org.joda.time.DateTime
import org.springframework.util.Assert
import com.mysema.query.BooleanBuilder
import com.mysema.query.jpa.JPQLSubQuery
import com.mysema.query.types.EntityPath
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.expr.ComparableExpressionBase
import com.mysema.query.types.path.StringPath
import fi.finlit.edith.EDITH
import fi.finlit.edith.dto.NoteSearchInfo
import fi.finlit.edith.dto.OrderBy
import fi.finlit.edith.dto.UserInfo
import fi.finlit.edith.sql.domain._
import fi.finlit.edith.ui.services.AuthService
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.ServiceException
import fi.finlit.edith.ui.services.UserDao
import NoteDaoImpl._
//remove if not needed
import scala.collection.JavaConversions._

object NoteDaoImpl {

  private class LoopContext private () {

    private var note = null

    private var text = null

    private var paragraphs: Paragraph = _

    private var counter = 0

    private var inBib: Boolean = _

    private var inA: Boolean = _

    private var reference: String = _

    private var url: String = _
  }
}

class NoteDaoImpl(@Inject userDao: UserDao, @Inject authService: AuthService, @Inject @Symbol(EDITH.EXTENDED_TERM) extendedTerm: Boolean) extends AbstractDao[Note] with NoteDao {

  override def getById(id: java.lang.Long): Note = {
    getSession.get(classOf[Note], id).asInstanceOf[Note]
  }

  override def createComment(note: Note, message: String): NoteComment = {
    var comment = new NoteComment(note, message, authService.getUsername)
    note.addComment(comment)
    comment.setCreatedAt(new DateTime())
    getSession.save(comment)
    comment
  }

  private def sub(entityPath: EntityPath[_]): JPQLSubQuery = new JPQLSubQuery().from(entityPath)

  override def findNotes(search: NoteSearchInfo): GridDataSource = {
    createGridDataSource(note, getOrderBy(search), false, notesQuery(search).getValue)
  }

  private def notesQuery(search: NoteSearchInfo): BooleanBuilder = {
    var builder = new BooleanBuilder()
    if (search.isOrphans && !search.isIncludeAllDocs) {
      builder.and(note.documentNoteCount eq 0)
    } else if (search.isOrphans && search.isIncludeAllDocs) {
      builder.and(note.documentNoteCount.goe(0))
    } else if (!search.isOrphans && search.isIncludeAllDocs) {
      builder.and(note.documentNoteCount gt 0)
    } else if (!search.isIncludeAllDocs && (!search.getPaths.isEmpty || !search.getDocuments.isEmpty)) {
      var filter = new BooleanBuilder()
      for (path <- search.getPaths) {
        filter.or(documentNote.document.path.startsWith(path))
      }
      if (!search.getDocuments.isEmpty) {
        filter.or(documentNote.document.in(search.getDocuments))
      }
      var subQuery = sub(documentNote)
      subQuery.where(documentNote.note eq note, documentNote.deleted eq false, filter)
      builder.and(subQuery.exists())
    }
    if (search.getLanguage != null) {
      builder.and(note.term.language eq search.getLanguage)
    }
    if (StringUtils.isNotBlank(search.getFullText)) {
      var filter = new BooleanBuilder()
      for (path <- Arrays.asList(note.lemma, note.description, note.sources, note.comments.any().message)) {
        filter.or(path.containsIgnoreCase(search.getFullText))
      }
      var term = QTerm.term
      filter.or(sub(term).where(term eq note.term, term.basicForm.containsIgnoreCase(search.getFullText).or(term.meaning.containsIgnoreCase(search.getFullText))).exists())
      builder.and(filter)
    }
    if (!search.getCreators.isEmpty) {
      var filter = new BooleanBuilder()
      var usernames = new ArrayList[String](search.getCreators.size)
      for (userInfo <- search.getCreators) {
        filter.or(note.allEditors.contains(userDao.getByUsername(userInfo.getUsername)))
        usernames.add(userInfo.getUsername)
      }
      filter.or(note.lastEditedBy.username.in(usernames))
      builder.and(filter)
    }
    if (!search.getNoteFormats.isEmpty) {
      builder.and(note.format.in(search.getNoteFormats))
    }
    if (!search.getNoteTypes.isEmpty) {
      var filter = new BooleanBuilder()
      for (`type` <- search.getNoteTypes) {
        filter.or(note.types.contains(`type`))
      }
      builder.and(filter)
    }
    builder
  }

  override def queryNotes(searchTerm: String): GridDataSource = {
    Assert.notNull(searchTerm)
    var builder = new BooleanBuilder()
    if (searchTerm != "*") {
      for (path <- Arrays.asList(note.lemma, note.term.basicForm, note.term.meaning)) {
        builder.or(path.containsIgnoreCase(searchTerm))
      }
    }
    builder.and(note.deleted.isFalse)
    createGridDataSource(note, note.lemma.asc(), false, builder.getValue)
  }

  private def getOrderBy(searchInfo: NoteSearchInfo): OrderSpecifier[_] = {
    var comparable: ComparableExpressionBase[_] = null
    var orderBy = if (searchInfo.getOrderBy == null) OrderBy.LEMMA else searchInfo.getOrderBy
    orderBy match {
      case KEYTERM => comparable = note.term.basicForm
      case DATE => comparable = note.editedOn
      case USER => comparable = note.lastEditedBy.username.toLowerCase()
      case STATUS => comparable = note.status
      case _ => comparable = note.lemma.toLowerCase()
    }
    if (searchInfo.isAscending) comparable.asc() else comparable.desc()
  }

  override def getOrphanIds(): List[Long] = {
    query.from(note).where(sub(documentNote).where(documentNote.note eq note).notExists()).list(note.id)
  }

  override def createDocumentNote(n: Note, document: Document, longText: String): DocumentNote = {
    createDocumentNote(new DocumentNote(), n, document, longText, 0)
  }

  override def createDocumentNote(documentNote: DocumentNote, 
      n: Note, 
      document: Document, 
      longText: String, 
      position: Int): DocumentNote = {
    var createdBy = userDao.getCurrentUser
    var currentTime = System.currentTimeMillis()
    documentNote.setCreatedOn(currentTime)
    n.setEditedOn(currentTime)
    n.setLastEditedBy(createdBy)
    n.addEditor(createdBy)
    documentNote.setFullSelection(longText)
    var createdLemma = Note.createLemmaFromLongText(longText)
    if (n.getLemma == null && !extendedTerm) {
      n.setLemma(createdLemma)
    }
    var abbreviation = StringUtils.abbreviate(longText, 85)
    if (extendedTerm && n.getTerm != null && n.getTerm.getBasicForm == null) {
      n.getTerm.setBasicForm(abbreviation)
    }
    if (extendedTerm && documentNote.getShortenedSelection == null) {
      documentNote.setShortenedSelection(abbreviation)
    }
    documentNote.setDocument(document)
    documentNote.setNote(n)
    n.incDocumentNoteCount()
    documentNote.setPosition(position)
    getSession.save(n)
    getSession.save(documentNote)
    documentNote
  }

  private def handleEndElement(reader: XMLStreamReader, data: LoopContext) {
    var localName = reader.getLocalName
    if (localName == "note") {
      data.note.setLastEditedBy(userDao.getCurrentUser)
      data.note.setEditedOn(System.currentTimeMillis())
      save(data.note)
      data.counter += 1
    } else if (localName == "lemma") {
      data.note.setLemma(data.text)
    } else if (localName == "lemma-meaning") {
      data.note.setLemmaMeaning(data.text)
    } else if (localName == "source") {
      data.note.setSources(data.paragraphs.toString)
      data.paragraphs = null
    } else if (localName == "description") {
      data.note.setDescription(data.paragraphs.toString)
      data.paragraphs = null
    } else if (localName == "bibliograph") {
      data.inBib = false
      data.reference = null
    } else if (localName == "a") {
      data.inA = false
      data.url = null
    }
  }

  private def handleStartElement(reader: XMLStreamReader, data: LoopContext) {
    var localName = reader.getLocalName
    if (localName == "note") {
      data.note = new Note()
      if (extendedTerm) {
        data.note.setTerm(new Term())
      }
    } else if (localName == "source" || localName == "description") {
      data.paragraphs = new Paragraph()
    }
    if (localName == "bibliograph") {
      data.inBib = true
      if (reader.getAttributeCount > 0) {
        data.reference = reader.getAttributeValue(0)
      }
    } else if (localName == "a") {
      data.inA = true
      if (reader.getAttributeCount > 0) {
        data.url = reader.getAttributeValue(0)
      }
    }
  }

  override def importNotes(file: File): Int = {
    var factory = XMLInputFactory.newInstance()
    var reader: XMLStreamReader = null
    try {
      reader = factory.createXMLStreamReader(new FileInputStream(file))
    } catch {
      case e: XMLStreamException => throw new ServiceException(e)
      case e: FileNotFoundException => throw new ServiceException(e)
    }
    var data = new LoopContext()
    while (true) {
      var event = -1
      try {
        event = reader.next()
      } catch {
        case e: XMLStreamException => throw new ServiceException(e)
      }
      if (event == XMLStreamConstants.START_ELEMENT) {
        handleStartElement(reader, data)
      } else if (event == XMLStreamConstants.END_ELEMENT) {
        handleEndElement(reader, data)
      } else if (event == XMLStreamConstants.CHARACTERS) {
        if (data.paragraphs == null) {
          data.text = reader.getText.replaceAll("\\s+", " ")
        } else {
          var text = reader.getText.replaceAll("\\s+", " ")
          if (data.inBib) {
            var el = new LinkElement(text)
            if (data.reference != null) {
              el.setReference(data.reference)
            }
            data.paragraphs.addElement(el)
          } else if (data.inA) {
            var el = new UrlElement(text)
            if (data.url != null) {
              el.setUrl(data.url)
            }
          } else {
            data.paragraphs.addElement(new StringElement(text))
          }
        }
      } else if (event == XMLStreamConstants.END_DOCUMENT) {
        try {
          reader.close()
        } catch {
          case e: XMLStreamException => throw new ServiceException(e)
        }
        //break
      }
    }
    data.counter
  }

  override def queryDictionary(searchTerm: String): GridDataSource = {
    Assert.notNull(searchTerm)
    if (searchTerm != "*") {
      var builder = new BooleanBuilder()
      builder.or(term.basicForm.containsIgnoreCase(searchTerm))
      builder.or(term.meaning.containsIgnoreCase(searchTerm))
      return createGridDataSource(term, term.basicForm.lower().asc(), false, builder.getValue)
    }
    createGridDataSource(term, term.basicForm.lower().asc(), false, null)
  }

  override def queryPersons(searchTerm: String): GridDataSource = {
    Assert.notNull(searchTerm)
    if (searchTerm != "*") {
      var builder = new BooleanBuilder()
      builder.or(person.normalized.first.containsIgnoreCase(searchTerm))
      builder.or(person.normalized.last.containsIgnoreCase(searchTerm))
      return createGridDataSource(person, person.normalized.last.lower().asc(), false, builder.getValue)
    }
    createGridDataSource(person, person.normalized.last.asc(), false, null)
  }

  override def queryPlaces(searchTerm: String): GridDataSource = {
    Assert.notNull(searchTerm)
    if (searchTerm != "*") {
      var builder = new BooleanBuilder()
      builder.or(place.normalized.last.containsIgnoreCase(searchTerm))
      return createGridDataSource(place, place.normalized.last.lower().asc(), false, builder.getValue)
    }
    createGridDataSource(place, place.normalized.last.asc(), false, null)
  }

  override def removeComment(commentId: java.lang.Long): NoteComment = {
    var comment = getSession.get(classOf[NoteComment], commentId).asInstanceOf[NoteComment]
    getSession.delete(comment)
    comment.getNote.removeComment(comment)
    comment
  }

  override def save(note: Note) {
    getSession.save(note)
  }

  override def remove(note: Note) {
    note.setDeleted(true)
    save(note)
  }

  override def removeNotes(notes: Collection[Note]) {
    for (note <- notes) {
      remove(note)
    }
  }

  override def saveAsNew(note: Note) {
    var session = getSession
    session.evict(note)
    note.setId(null)
    note.setDocumentNoteCount(0)
    session.save(note)
  }
}
