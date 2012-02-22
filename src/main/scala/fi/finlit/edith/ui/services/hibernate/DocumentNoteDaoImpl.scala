package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QDocumentNote.documentNote
import java.util.List
import org.apache.tapestry5.ioc.annotations.Inject
import org.springframework.util.Assert
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.User
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.ServiceException
import fi.finlit.edith.ui.services.UserDao
//remove if not needed
import scala.collection.JavaConversions._

class DocumentNoteDaoImpl(@Inject userDao: UserDao) extends AbstractDao[DocumentNote] with DocumentNoteDao {

  override def getOfDocument(document: Document): List[DocumentNote] = {
    Assert.notNull(document)
    query.from(documentNote).where(documentNote.document eq document, documentNote.deleted eq false).orderBy(documentNote.createdOn.asc()).list(documentNote)
  }

  override def getPublishableNotesOfDocument(document: Document): List[DocumentNote] = {
    query.from(documentNote).where(documentNote.document eq document, documentNote.deleted.isFalse, documentNote.publishable.isTrue).orderBy(documentNote.position.asc()).list(documentNote)
  }

  override def remove(docNote: DocumentNote) {
    Assert.notNull(docNote, "note was null")
    docNote.setDeleted(true)
    docNote.getNote.decDocumentNoteCount()
  }

  override def save(docNote: DocumentNote): DocumentNote = {
    if (docNote.getNote == null) {
      throw new ServiceException("Note was null for " + docNote)
    }
    var createdBy = userDao.getCurrentUser
    var currentTime = System.currentTimeMillis()
    docNote.setCreatedOn(currentTime)
    docNote.getNote.setEditedOn(currentTime)
    docNote.getNote.setLastEditedBy(createdBy)
    docNote.getNote.getAllEditors.add(createdBy)
    docNote
  }

  override def getOfNote(noteId: java.lang.Long): List[DocumentNote] = {
    Assert.notNull(noteId)
    query.from(documentNote).where(documentNote.note.id eq noteId, documentNote.deleted.isFalse).list(documentNote)
  }

  override def getDocumentNoteCount(note: Note): Int = note.getDocumentNoteCount

  override def getNoteCountForDocument(id: java.lang.Long): Long = {
    query.from(documentNote).where(documentNote.document.id eq id, documentNote.deleted.isFalse).count()
  }

  override def getOfTerm(termId: java.lang.Long): List[DocumentNote] = {
    Assert.notNull(termId)
    query.from(documentNote).where(documentNote.note.term.id eq termId, documentNote.deleted.isFalse).list(documentNote)
  }

  override def getOfPerson(personId: java.lang.Long): List[DocumentNote] = {
    Assert.notNull(personId)
    query.from(documentNote).where(documentNote.note.person.id eq personId, documentNote.deleted.isFalse).list(documentNote)
  }

  override def getOfPlace(placeId: java.lang.Long): List[DocumentNote] = {
    Assert.notNull(placeId)
    query.from(documentNote).where(documentNote.note.place.id eq placeId, documentNote.deleted.isFalse).list(documentNote)
  }

  override def getById(id: java.lang.Long): DocumentNote = {
    getSession.get(classOf[DocumentNote], id).asInstanceOf[DocumentNote]
  }
}
