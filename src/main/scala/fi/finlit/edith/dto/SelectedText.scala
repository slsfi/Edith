package fi.finlit.edith.dto

import java.util.regex.Pattern
import org.apache.commons.lang.StringUtils
import SelectedText._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object SelectedText {

  private val HYPHEN = Pattern.compile("-")
}

class SelectedText() {

  @BeanProperty
  var selection: String = _

  @BeanProperty
  var startId: String = _

  @BeanProperty
  var endId: String = _

  @BeanProperty
  var startIndex = 1

  @BeanProperty
  var endIndex = 1

  def this(startId: String, 
      endId: String, 
      startIndex: Int, 
      endIndex: Int, 
      selection: String) {
    this.startId = startId
    this.endId = endId
    this.startIndex = startIndex
    this.endIndex = endIndex
    this.selection = selection
  }

  def this(startId: String, endId: String, selection: String) {
    this(startId, endId, 1, 1, selection)
  }

  def isValid(): Boolean = {
    hasSelection() && hasStart() && hasEnd()
  }

  private def hasSelection(): Boolean = {
    selection != null && selection.trim().length() > 0
  }

  private def hasStart(): Boolean = {
    startId != null && startId.trim().length() > 0
  }

  private def hasEnd(): Boolean = {
    endId != null && endId.trim().length() > 0
  }

  def getFirstWord(): String = {
    var words = StringUtils.split(selection)
    words(0)
  }

  def getLastWord(): String = {
    var words = StringUtils.split(selection)
    words(words.length - 1)
  }

  def isStartChildOfEnd(): Boolean = {
    startId.startsWith(endId) && endId.length() < startId.length()
  }

  def howDeepIsStartInEnd(): Int = {
    howDeepIsElementInElement(startId, endId)
  }

  def howDeepIsEndInStart(): Int = {
    howDeepIsElementInElement(endId, startId)
  }

  private def howDeepIsElementInElement(el1: String, el2: String): Int = {
    var n = 0
    var el1s[] = HYPHEN.split(el1)
    var el2s[] = HYPHEN.split(el2)
    for (i <- 0 until el1s.length) {
      if (i < el2s.length) {
        if (el1s(i) != el2s(i)) {
          return -1
        }
      } else {
        n
      }
    }
    n
  }

  def isEndChildOfStart(): Boolean = {
    endId.startsWith(startId) && endId.length() > startId.length()
  }

  override def toString(): String = {
    var buffer = new StringBuffer()
    buffer.append(startId + "[" + startIndex + "] , ")
    buffer.append(endId + "[" + endIndex + "] : ")
    buffer.append(selection)
    buffer.toString
  }
}
