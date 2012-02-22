package fi.finlit.edith.sql.domain

//remove if not needed
import scala.collection.JavaConversions._

object NoteStatus {

  class NoteStatus {
  }

  val INITIAL = new NoteStatus()

  val DRAFT = new NoteStatus()

  val FINISHED = new NoteStatus()
}
