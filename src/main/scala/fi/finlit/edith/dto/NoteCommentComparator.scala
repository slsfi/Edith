package fi.finlit.edith.dto

import java.util.Comparator
import fi.finlit.edith.sql.domain.NoteComment
import NoteCommentComparator._
//remove if not needed
import scala.collection.JavaConversions._

object NoteCommentComparator {

  val ASC = new NoteCommentComparator()
}

class NoteCommentComparator extends Comparator[NoteComment] {

  override def compare(o1: NoteComment, o2: NoteComment): Int = {
    if (o1.getCreatedAt == o2.getCreatedAt) {
      return 0
    }
    if (o1.getCreatedAt.isBefore(o2.getCreatedAt)) -1 else 1
  }
}
