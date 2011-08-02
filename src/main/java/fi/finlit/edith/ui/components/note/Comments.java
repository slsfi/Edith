package fi.finlit.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.dto.NoteCommentComparator;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteComment;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.NoteDao;

@SuppressWarnings("unused")
public class Comments {

    @InjectPage
    private Annotate page;

    @Parameter
    @Property
    private Note noteOnEdit;

    @Property
    private NoteComment comment;

    @Property
    private String newCommentMessage;

    private List<NoteComment> comments;

    @Inject
    @Property
    private Block commentsBlock;

    @Inject
    private NoteDao noteDao;

    private static List<NoteComment> getSortedComments(Set<NoteComment> c) {
        List<NoteComment> rv = new ArrayList<NoteComment>(c);
        Collections.sort(rv, NoteCommentComparator.ASC);
        return rv;
    }

    public List<NoteComment> getComments() {
        if (noteOnEdit != null && comments == null) {
            comments = getSortedComments(noteOnEdit.getComments());
        }

        return comments;
    }

    public String getCommentsSize() {
        return getComments() != null ? "(" + getComments().size() + ")" : "";
    }

    void onPrepareFromCommentForm(long noteId) {
        if (noteOnEdit == null) {
            System.err.println("onPrepareFromCommentForm with noteid " + noteId);
            noteOnEdit = noteDao.getById(noteId);
        }
    }

    Object onDeleteComment(long noteId, long commentId) {
        noteDao.removeComment(commentId);
        noteOnEdit = noteDao.getById(noteId);
        comments = null;
        return commentsBlock;
    }

    Object onSuccessFromCommentForm() {
        System.err.println("onSuccessFromCommentForm with comment " + newCommentMessage
                + " on note " + noteOnEdit);
        Set<NoteComment> conceptComments = noteOnEdit.getComments();
        if (newCommentMessage != null) {
            conceptComments.add(noteDao.createComment(noteOnEdit, newCommentMessage));
            newCommentMessage = null;
        }
        comments = getSortedComments(conceptComments);
        System.err.println("onSuccessFromCommentForm --");
        return commentsBlock;
    }

}
