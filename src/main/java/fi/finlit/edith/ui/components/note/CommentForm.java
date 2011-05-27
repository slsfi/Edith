package fi.finlit.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteCommentComparator;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.NoteRepository;

@SuppressWarnings("unused")
public class CommentForm {

    @InjectPage
    private Annotate page;

    @Parameter
    @Property
    private Note noteOnEdit;

    @Property
    private NoteComment comment;

    @Property
    private String newCommentMessage;

    @Property
    private List<NoteComment> comments;

    @InjectComponent
    @Property
    private Zone commentZone;

    @Inject
    private NoteRepository noteRepository;

    private static List<NoteComment> getSortedComments(Set<NoteComment> c) {
        List<NoteComment> rv = new ArrayList<NoteComment>(c);
        Collections.sort(rv, NoteCommentComparator.DESC);
        return rv;
    }

    void onPrepareFromCommentForm(String noteId) {
        if (noteOnEdit == null) {
            System.err.println("onPrepareFromCommentForm");
            noteOnEdit = noteRepository.getById(noteId);
        }
    }

    Object onDeleteComment(String noteId, String commentId) {
        NoteComment deletedComment = noteRepository.removeComment(commentId);
        noteOnEdit = noteRepository.getById(noteId);
        comments = getSortedComments(noteOnEdit.getConcept(page.isSlsMode()).getComments());
        comments.remove(deletedComment);
        return commentZone.getBody();
    }

    Object onSuccessFromCommentForm() {
        System.err.println("onSuccessFromCommentForm");
        Concept concept = noteOnEdit.getConcept(page.isSlsMode());
        comments = getSortedComments(concept.getComments());
        if (newCommentMessage != null) {
            comments.add(0, noteRepository.createComment(concept, newCommentMessage));
            newCommentMessage = null;
        }
        System.err.println("onSuccessFromCommentForm --");
        return commentZone.getBody();
    }

}
