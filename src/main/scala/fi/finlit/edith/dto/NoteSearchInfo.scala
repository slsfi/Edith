package fi.finlit.edith.dto

import java.util.HashSet
import java.util.Set
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.NoteFormat
import fi.finlit.edith.sql.domain.NoteType
import fi.finlit.edith.sql.domain.TermLanguage
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class NoteSearchInfo() {

  @BeanProperty
  var documents = new HashSet[Document]()

  @BeanProperty
  var paths = new HashSet[String]()

  @BeanProperty
  var noteTypes = new HashSet[NoteType]()

  @BeanProperty
  var noteFormats = new HashSet[NoteFormat]()

  @BeanProperty
  var creators = new HashSet[UserInfo]()

  @BeanProperty
  var language: TermLanguage = _

  @BeanProperty
  var orderBy: OrderBy = _

  @BeanProperty
  var ascending = true

  @BeanProperty
  var orphans = false

  @BeanProperty
  var includeAllDocs = false

  @BeanProperty
  var currentDocument: Document = _

  @BeanProperty
  var fullText: String = _

  def this(document: Document) {
    currentDocument = document
  }

  override def toString(): String = {
    "DocumentNoteSearchInfo [noteTypes=" + noteTypes + ", noteFormats=" + noteFormats + ", creators=" + creators + ", language=" + language + ", orderBy=" + orderBy + ", ascending=" + ascending + ", orphans=" + orphans + ", includeAllDocs=" + includeAllDocs + ", fullText=" + fullText + "]"
  }
}
