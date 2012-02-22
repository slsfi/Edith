package fi.finlit.edith.ui.components.document

import java.io.File
import java.io.IOException
import org.apache.tapestry5.PersistenceConstants
import org.apache.tapestry5.annotations.Persist
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.apache.tapestry5.upload.services.UploadedFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import fi.finlit.edith.EDITH
import fi.finlit.edith.ui.services.DocumentDao
import Upload._
//remove if not needed
import scala.collection.JavaConversions._

object Upload {

  private val logger = LoggerFactory.getLogger(classOf[Upload])
}

class Upload {

  @Inject
  private var documentDao: DocumentDao = _

  @Inject
  @Symbol(EDITH.SVN_DOCUMENT_ROOT)
  private var documentRoot: String = _

  @Property
  private var file: UploadedFile = _

  @Property
  private var path: String = _

  @Persist(PersistenceConstants.FLASH)
  @Property
  private var message: String = _

  @Inject
  private var messages: Messages = _

  def onSuccess() {
    var tempFile = File.createTempFile("upload", null)
    var uploadPath = if (path == null) documentRoot else path
    try {
      file.write(tempFile)
      if (file.getFileName.endsWith(".zip")) {
        documentDao.addDocumentsFromZip(uploadPath, tempFile)
        message = messages.format("documents-stored-msg", file.getFileName)
      } else {
        documentDao.addDocument(uploadPath + "/" + file.getFileName, tempFile)
        message = messages.format("document-stored-msg", file.getFileName)
      }
    } finally {
      if (!tempFile.delete()) {
        logger.error("Delete of " + tempFile.getAbsolutePath + " failed")
      }
    }
  }
}
