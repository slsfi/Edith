package fi.finlit.edith.ui.pages

import java.io.File
import java.io.IOException
import org.apache.tapestry5.PersistenceConstants
import org.apache.tapestry5.annotations.Persist
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.upload.services.UploadedFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import fi.finlit.edith.ui.services.NoteDao
import NoteImport._
//remove if not needed
import scala.collection.JavaConversions._

object NoteImport {

  private val logger = LoggerFactory.getLogger(classOf[NoteImport])
}

class NoteImport {

  @Inject
  private var noteDao: NoteDao = _

  @Property
  private var file: UploadedFile = _

  @Inject
  private var messages: Messages = _

  @Persist(PersistenceConstants.FLASH)
  @Property
  private var message: String = _

  def onActivate() {
  }

  def onSuccess() {
    var tempFile = File.createTempFile("upload", null)
    try {
      file.write(tempFile)
      var rv = noteDao.importNotes(tempFile)
      message = messages.format("notes-imported-msg", rv)
    } finally {
      if (!tempFile.delete() && !tempFile.delete()) {
        logger.error("Delete of " + tempFile.getAbsolutePath + " failed")
      }
    }
  }
}
