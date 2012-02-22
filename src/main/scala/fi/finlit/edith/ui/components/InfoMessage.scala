package fi.finlit.edith.ui.components

import org.apache.tapestry5.Block
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.ioc.annotations.Inject
//remove if not needed
import scala.collection.JavaConversions._

class InfoMessage {

  @Inject
  @Property
  private var infoMessageBlock: Block = _

  @Inject
  @Property
  private var messages: Messages = _

  @Property
  private var info: String = _

  @Property
  private var error: String = _

  def getBlock(): Block = infoMessageBlock

  def addInfoMsg(key: String) {
    info = key
  }

  def getInfoMsg(): String = {
    if (info != null) messages.get(info) else ""
  }

  def getErrorMsg(): String = {
    if (error != null) messages.get(error) else ""
  }

  def addErrorMsg(key: String) {
    error = key
  }
}
