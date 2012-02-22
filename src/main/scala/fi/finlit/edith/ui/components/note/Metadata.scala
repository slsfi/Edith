package fi.finlit.edith.ui.components.note

import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import fi.finlit.edith.sql.domain.Note
//remove if not needed
import scala.collection.JavaConversions._

class Metadata {

  @Property
  @Parameter(required = true)
  private var note: Note = _
}
