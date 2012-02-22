package fi.finlit.edith.ui.services.svn

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.List
import java.util.Map
import org.tmatesoft.svn.core.SVNException
//remove if not needed
import scala.collection.JavaConversions._

/**
 * SubversionService provides Subversion client functionality
 *
 * @author tiwe
 * @version $Id$
 */
trait SubversionService {

  /**
   * Commits changes of a file into the repository.
   *
   * @param svnPath
   * @param revision
   * @param username
   * @param callback
   */
  def commit(svnPath: String, 
      revision: Long, 
      username: String, 
      callback: UpdateCallback): Long

  /**
   * Delete the given svn path
   *
   * @param svnPath
   */
  def delete(svnPath: String): Unit

  /**
   * Deletes the repository directory and related caches and working copy directories.
   */
  def destroy(): Unit

  /**
   * Get directory entries
   *
   * @param svnFolder
   * @param revision
   * @return collection of child names
   */
  def getEntries(svnFolder: String, revision: Long): Map[String, String]

  /**
   * Retrieves the latest revision number.
   *
   * @return
   */
  def getLatestRevision(): Long

  /**
   * Get read access to given svn path with given revision
   *
   * @param svnPath svn path of file
   * @param revision
   * @return
   * @throws IOException
   */
  def getStream(svnPath: String, revision: Long): InputStream

  /**
   * Import the given file into SVN (SVN add + import)
   *
   * @param svnPath target path
   * @param file file to be imported
   * @return revision number of commit
   */
  def importFile(svnPath: String, file: File): Long

  /**
   * Creates the repository and adds the directory structure.
   */
  def initialize(): Unit

  def getFileItems(path: String, revision: Int): List[FileItem]

  def move(oldPath: String, newPath: String): Long
}
