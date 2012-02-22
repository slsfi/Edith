package fi.finlit.edith.sql.domain

//remove if not needed
import scala.collection.JavaConversions._

object NoteType {

  class NoteType {
  }

  val WORD_EXPLANATION = new NoteType()

  val LITERARY = new NoteType()

  val HISTORICAL = new NoteType()

  val DICTUM = new NoteType()

  val CRITIQUE = new NoteType()

  val TITLE = new NoteType()

  val TRANSLATION = new NoteType()

  val REFERENCE = new NoteType()
}
