package fi.finlit.edith.ui.services

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.Collection
import java.util.List
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.dto.SelectedText
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.ui.services.svn.FileItemWithDocumentId
//remove if not needed
import scala.collection.JavaConversions._

trait DocumentDao extends Dao[Document, Long] {

  /**
   * Import the given File to the given svnPath
   *
   * @param svnPath
   * @param file
   */
  @CommitAfter
  def addDocument(svnPath: String, file: File): Unit

  /**
   * Import documents from the given ZIP file
   *
   * @param parentSvnPath
   * @param file
   * @return amount of imported documents
   */
  @CommitAfter
  def addDocumentsFromZip(parentSvnPath: String, file: File): Int

  /**
   * Add the given note for the given Document
   *
   * @param docRevision
   * @param selection
   * @return
   * @throws IOException
   * @throws NoteAdditionFailedException
   */
  @CommitAfter
  def addNote(note: Note, document: Document, selection: SelectedText): DocumentNote

  /**
   * Get a Document handle for the given path
   *
   * @param svnPath
   * @return
   */
  @CommitAfter
  def getDocumentForPath(svnPath: String): Document

  /**
   * Get the Documents of the given directory path and its subpaths
   *
   * @param svnFolder
   * @return
   */
  @CommitAfter
  def getDocumentsOfFolder(svnFolder: String): List[Document]

  /**
   * Get the file for the given document for reading
   *
   * @param docRevision
   * @return
   * @throws IOException
   */
  def getDocumentStream(document: Document): InputStream

  /**
   * Remove the given anchors from the given Document
   *
   * @param docRevision
   * @param notes
   * @throws IOException
   */
  @Deprecated
  @CommitAfter
  def removeDocumentNotes(document: Document, notes: DocumentNote*): Unit

  /**
   * Update the boundaries of the given note
   *
   * @param note
   * @param selection
   * @throws IOException
   */
  @Deprecated
  @CommitAfter
  def updateNote(note: DocumentNote, selection: SelectedText): DocumentNote

  /**
   * Remove the given document
   *
   * @param doc
   */
  @CommitAfter
  def remove(doc: Document): Unit

  /**
   * Remove the document by id.
   */
  @CommitAfter
  def remove(id: java.lang.Long): Unit

  /**
   * Remove the given documents
   *
   * @param documents
   */
  @CommitAfter
  def removeAll(documents: Collection[Document]): Unit

  @CommitAfter
  def rename(id: java.lang.Long, newPath: String): Unit

  @CommitAfter
  def fromPath(path: String, id: java.lang.Long): List[FileItemWithDocumentId]
}
