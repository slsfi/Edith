package fi.finlit.edith.ui.components.note

import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Set
import org.apache.tapestry5.Block
import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.dto.NoteCommentComparator
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.NoteComment
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.NoteDao
import Comments._
//remove if not needed
import scala.collection.JavaConversions._

object Comments {

  private def getSortedComments(c: Set[NoteComment]): List[NoteComment] = {
    var rv = new ArrayList[NoteComment](c)
    Collections.sort(rv, NoteCommentComparator.ASC)
    rv
  }
}

class Comments {

  @InjectPage
  private var page: Annotate = _

  @Parameter
  @Property
  private var noteOnEdit: Note = _

  @Property
  private var comment: NoteComment = _

  @Property
  private var newCommentMessage: String = _

  private var comments: List[NoteComment] = _

  @Inject
  @Property
  private var commentsBlock: Block = _

  @Inject
  private var noteDao: NoteDao = _

  def getComments(): List[NoteComment] = {
    if (noteOnEdit != null && comments == null) {
      comments = getSortedComments(noteOnEdit.getComments)
    }
    comments
  }

  def getCommentsSize(): String = {
    if (getComments != null) "(" + getComments.size + ")" else ""
  }

  def onPrepareFromCommentForm(noteId: Long) {
    if (noteOnEdit == null) {
      noteOnEdit = noteDao.getById(noteId)
    }
  }

  def onDeleteComment(noteId: Long, commentId: Long): AnyRef = {
    noteDao.removeComment(commentId)
    noteOnEdit = noteDao.getById(noteId)
    comments = null
    commentsBlock
  }

  def onSuccessFromCommentForm(): AnyRef = {
    var conceptComments = noteOnEdit.getComments
    if (newCommentMessage != null) {
      conceptComments.add(noteDao.createComment(noteOnEdit, newCommentMessage))
      newCommentMessage = null
    }
    comments = getSortedComments(conceptComments)
    commentsBlock
  }
}
