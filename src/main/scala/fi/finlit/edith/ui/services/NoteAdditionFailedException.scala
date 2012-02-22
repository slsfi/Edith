package fi.finlit.edith.ui.services

import fi.finlit.edith.dto.SelectedText
import NoteAdditionFailedException._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object NoteAdditionFailedException {

  private def createDescription(localId: String, startMatched: Boolean, endMatched: Boolean): String = {
    var builder = new StringBuilder()
    builder.append("Failed to add selected text for note #").append(localId)
    if (!startMatched) {
      builder.append("; start was not matched")
    }
    if (!endMatched) {
      builder.append("; end was not matched")
    }
    builder.toString
  }
}

class NoteAdditionFailedException(@BeanProperty val selectedText: SelectedText, 
    @BeanProperty val localId: String, 
    startMatched: Boolean, 
    endMatched: Boolean) extends Exception(createDescription(localId, startMatched, endMatched)) {


}
