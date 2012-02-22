package fi.finlit.edith.ui.services

import java.util.List
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
//remove if not needed
import scala.collection.JavaConversions._

trait DocumentNoteDao extends Dao[DocumentNote, Long] {

  /**
   * @param docNote
   */
  @CommitAfter
  def remove(docNote: DocumentNote): Unit

  /**
   * @param docNote
   * @return
   */
  @CommitAfter
  def save(docNote: DocumentNote): DocumentNote

  /**
   * Get the note revisions of the given document revision
   *
   * @param document
   * @param revision
   * @return
   */
  def getOfDocument(document: Document): List[DocumentNote]

  /**
   * Get the document notes of the given note.
   *
   * @param noteId
   * @return
   */
  def getOfNote(noteId: java.lang.Long): List[DocumentNote]

  /**
   * Get the DocumentNotes of the given Person.
   *
   * @param personId
   * @return
   */
  def getOfPerson(personId: java.lang.Long): List[DocumentNote]

  /**
   * Get the DocumentNotes of the given Place.
   *
   * @param personId
   * @return
   */
  def getOfPlace(placeId: java.lang.Long): List[DocumentNote]

  /**
   * Returns all the document notes attached to the term.
   *
   * @param termId
   * @return
   */
  def getOfTerm(termId: java.lang.Long): List[DocumentNote]

  /**
   * @param documentRevision
   * @return
   */
  def getPublishableNotesOfDocument(document: Document): List[DocumentNote]

  /**
   * @param note
   * @return
   */
  def getDocumentNoteCount(note: Note): Int

  def getNoteCountForDocument(id: java.lang.Long): Long
}
