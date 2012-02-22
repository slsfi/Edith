package fi.finlit.edith.sql.domain

//remove if not needed
import scala.collection.JavaConversions._

trait ParagraphElement {

  def copy(): ParagraphElement
}
