package fi.finlit.edith.ui.services.svn

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.UUID
import org.apache.commons.io.FileUtils
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tmatesoft.svn.core.SVNDirEntry
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNNodeKind
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNRevision
import fi.finlit.edith.EDITH
import SubversionServiceImpl._
//remove if not needed
import scala.collection.JavaConversions._

object SubversionServiceImpl {

  private val logger = LoggerFactory.getLogger(classOf[SubversionServiceImpl])

  FSRepositoryFactory.setup()
}

/**
 * SubversionServiceImpl is the default implementation of the SubversionService interface
 *
 * @author tiwe
 * @version $Id$
 */
class SubversionServiceImpl(@Inject @Symbol(EDITH.SVN_CACHE_DIR) svnCache: File, 
    @Inject @Symbol(EDITH.REPO_FILE_PROPERTY) svnRepo: File, 
    @Inject @Symbol(EDITH.REPO_URL_PROPERTY) repoURL: String, 
    @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) documentRoot: String, 
    @Inject @Symbol(EDITH.TEI_MATERIAL_ROOT) teiMaterialRoot: String) extends SubversionService {

  private var clientManager = SVNClientManager.newInstance()

  private val readCache = new File(svnCache + "/readCache")

  private val repoSvnURL: SVNURL = _

  private var svnRepository = null

  private val workingCopies = new File(svnCache + "/workingCopies")

  try {
    repoSvnURL = SVNURL.parseURIEncoded(repoURL)
  } catch {
    case e: SVNException => throw new SubversionException(e)
  }

  def setClientManager(clientManager: SVNClientManager) {
    this.clientManager = clientManager
  }

  def setSvnRepository(svnRepository: SVNRepository) {
    this.svnRepository = svnRepository
  }

  def checkout(destination: File, revision: Long) {
    try {
      clientManager.getUpdateClient.doCheckout(repoSvnURL.appendPath(documentRoot, true), destination, SVNRevision.create(revision), SVNRevision.create(revision), true)
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  def commit(file: File): Long = {
    try {
      clientManager.getCommitClient.doCommit(Array[File](file), true, file.getName + " committed", false, true).getNewRevision
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def commit(svnPath: String, 
      revision: Long, 
      username: String, 
      callback: UpdateCallback): Long = {
    var userCheckout = new File(workingCopies + "/" + username)
    var path = svnPath.substring(documentRoot.length())
    if (userCheckout.exists()) {
      try {
        update(userCheckout)
      } catch {
        case e: SubversionException => {
          try {
            FileUtils.cleanDirectory(userCheckout)
          } catch {
            case e1: IOException => throw new SubversionException("Exception when cleaning directory", e1)
          }
          userCheckout.delete()
          checkout(userCheckout, revision)
          logger.error(e.getMessage, e)
        }
      }
    } else {
      checkout(userCheckout, revision)
    }
    var file = new File(userCheckout + "/" + path)
    var tmp: File = null
    try {
      tmp = File.createTempFile(UUID.randomUUID().toString, null)
      var is = new FileInputStream(file)
      var os = new FileOutputStream(tmp)
      try {
        callback.update(is, os)
      } finally {
        os.close()
        is.close()
      }
      FileUtils.copyFile(tmp, file)
    } catch {
      case e: IOException => throw new SubversionException(e)
    } finally {
      if (tmp != null && !tmp.delete()) {
        logger.error("Delete of " + tmp.getAbsolutePath + " failed")
      }
    }
    var newRevision = commit(file)
    if (newRevision != -1) newRevision else getLatestRevision
  }

  override def delete(svnPath: String) {
    try {
      var targetURL = repoSvnURL.appendPath(svnPath, false)
      logger.info(clientManager.getCommitClient.doDelete(Array[SVNURL](targetURL), "removed " + svnPath).toString)
    } catch {
      case e: SVNException => throw new SubversionException(e.getMessage, e)
    }
  }

  override def destroy() {
    try {
      svnRepository.closeSession()
      svnRepository = null
      FileUtils.deleteDirectory(svnCache)
      FileUtils.deleteDirectory(svnRepo)
    } catch {
      case e: IOException => throw new SubversionException(e.getMessage, e)
    }
  }

  override def getEntries(svnFolder: String, revision: Long): Map[String, String] = {
    try {
      var entries = new ArrayList[SVNDirEntry]()
      svnRepository.getDir(svnFolder, revision, false, entries)
      var rv = new HashMap[String, String](entries.size)
      for (entry <- entries) {
        if (entry.getKind == SVNNodeKind.DIR) {
          rv.putAll(getEntries(svnFolder + "/" + entry.getName, revision))
        } else {
          rv.put(svnFolder + "/" + entry.getName, entry.getName)
        }
      }
      rv
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def getLatestRevision(): Long = {
    try {
      svnRepository.getLatestRevision
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def getStream(svnPath: String, revision: Long): InputStream = {
    try {
      var rev = revision
      if (rev == -1) {
        rev = getLatestRevision
      }
      var documentFolder = new File(readCache, URLEncoder.encode(svnPath, "UTF-8"))
      var documentFile = new File(documentFolder, String.valueOf(rev))
      if (!documentFile.exists()) {
        if (!documentFolder.exists() && !documentFolder.mkdirs()) {
          throw new IOException("Could not create directory: " + documentFolder.getAbsolutePath)
        }
        var out = new FileOutputStream(documentFile)
        try {
          svnRepository.getFile(svnPath, rev, null, out)
        } finally {
          out.close()
        }
      }
      if (documentFile.isFile) {
        return new FileInputStream(documentFile)
      }
      null
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def importFile(svnPath: String, file: File): Long = {
    try {
      clientManager.getCommitClient.doImport(file, repoSvnURL.appendPath(svnPath, false), svnPath + " added", true).getNewRevision
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def initialize() {
    logger.info("Initializing SVN repository on: " + svnRepo.getAbsolutePath)
    try {
      svnRepository = SVNRepositoryFactory.create(repoSvnURL)
      if (svnRepo.exists()) {
        return
      }
      SVNRepositoryFactory.createLocalRepository(svnRepo, true, false)
      clientManager.getCommitClient.doMkDir(Array[SVNURL](repoSvnURL.appendPath(documentRoot.split("/")(1), false), repoSvnURL.appendPath(documentRoot, false)), "created initial folders")
      if (new File(teiMaterialRoot).exists()) {
        for (file <- new File(teiMaterialRoot).listFiles()) {
          if (file.getName.endsWith(".svn")) {
            continue;
          }
          importFile(documentRoot + "/" + file.getName, file)
        }
      }
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  def update(file: File) {
    try {
      clientManager.getUpdateClient.doUpdate(file, SVNRevision.create(getLatestRevision), true)
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def getFileItems(path: String, revision: Int): List[FileItem] = {
    try {
      var entries = new ArrayList[SVNDirEntry]()
      svnRepository.getDir(path, revision, false, entries)
      var fileItems = new ArrayList[FileItem]()
      for (entry <- entries) {
        if (entry.getKind == SVNNodeKind.DIR) {
          var children = new ArrayList[SVNDirEntry]()
          svnRepository.getDir(path + "/" + entry.getRelativePath, revision, false, children)
          fileItems.add(new FileItem(entry.getName, path + "/" + entry.getRelativePath, true, new ArrayList[FileItem](), !children.isEmpty))
        } else {
          fileItems.add(new FileItem(entry.getName, path + "/" + entry.getRelativePath, false, null, false))
        }
      }
      fileItems
    } catch {
      case s: SVNException => throw new SubversionException(s.getMessage, s)
    }
  }

  override def move(oldPath: String, newPath: String): Long = {
    try {
      var userCheckout = new File(workingCopies + "/" + "timo")
      var path = oldPath.substring(documentRoot.length())
      if (userCheckout.exists()) {
        update(userCheckout)
      } else {
        checkout(userCheckout, -1)
      }
      var oldFile = new File(userCheckout + "/" + path)
      var newFile = new File(userCheckout + "/" + newPath.replace(documentRoot, ""))
      clientManager.getMoveClient.doMove(oldFile, newFile)
      commit(userCheckout)
    } catch {
      case e: SVNException => throw new SubversionException(e.getMessage, e)
    }
    getLatestRevision
  }
}
