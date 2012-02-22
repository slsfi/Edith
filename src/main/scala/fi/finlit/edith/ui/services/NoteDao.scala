package fi.finlit.edith.ui.services

import java.io.File
import java.util.Collection
import java.util.List
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.dto.NoteSearchInfo
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.NoteComment
//remove if not needed
import scala.collection.JavaConversions._

trait NoteDao extends Dao[Note, Long] {

  /**
   * Creates a comment for the given note.
   *
   * @param concept
   * @param message
   */
  @CommitAfter
  def createComment(note: Note, message: String): NoteComment

  @CommitAfter
  def createDocumentNote(documentNote: DocumentNote, 
      note: Note, 
      document: Document, 
      longText: String, 
      position: Int): DocumentNote

  /**
   * Import notes from the given file
   *
   * @param file
   * @throws Exception
   */
  @CommitAfter
  def importNotes(file: File): Int

  /**
   * Query for notes with the given search term
   *
   * @param searchTerm
   * @return
   */
  def queryDictionary(searchTerm: String): GridDataSource

  /**
   * Removes a NoteComment based on its id. Returns the deleted comment.
   *
   * @param commentId
   * @return
   */
  @CommitAfter
  def removeComment(commentId: java.lang.Long): NoteComment

  /**
   * @param searchTerm
   * @return
   */
  def queryPersons(searchTerm: String): GridDataSource

  /**
   * @param searchTerm
   * @return
   */
  def queryPlaces(searchTerm: String): GridDataSource

  /**
   * @param searchTerm
   * @return
   */
  def queryNotes(searchTerm: String): GridDataSource

  /**
   * @return
   */
  def getOrphanIds(): List[Long]

  def findNotes(search: NoteSearchInfo): GridDataSource

  /**
   * @param editedNote
   */
  @CommitAfter
  def save(editedNote: Note): Unit

  /**
   * Remove notes
   *
   * @param notes
   */
  @CommitAfter
  def removeNotes(notes: Collection[Note]): Unit

  /**
   * Remove note
   *
   * @param note
   */
  @CommitAfter
  def remove(note: Note): Unit

  @CommitAfter
  def createDocumentNote(note: Note, document: Document, longText: String): DocumentNote

  @CommitAfter
  def saveAsNew(noteOnEdit: Note): Unit
}
