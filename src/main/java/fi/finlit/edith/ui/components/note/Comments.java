package fi.finlit.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
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
    private NoteRepository noteRepository;
    
    private static List<NoteComment> getSortedComments(Set<NoteComment> c) {
        List<NoteComment> rv = new ArrayList<NoteComment>(c);
        Collections.sort(rv, NoteCommentComparator.ASC);
        return rv;
    }

    public List<NoteComment> getComments() {
        if (noteOnEdit != null && comments == null) {
            comments = getSortedComments(noteOnEdit.getConcept(page.isSlsMode()).getComments());
        }

        return comments;
    }
    
    void onPrepareFromCommentForm(String noteId) {
        if (noteOnEdit == null) {
            System.err.println("onPrepareFromCommentForm with noteid " + noteId);
            noteOnEdit = noteRepository.getById(noteId);
        }
    }

    Object onDeleteComment(String noteId, String commentId) {
        NoteComment deletedComment = noteRepository.removeComment(commentId);
        noteOnEdit = noteRepository.getById(noteId);
        comments = null;
        return commentsBlock;
    }

    Object onSuccessFromCommentForm() {
        System.err.println("onSuccessFromCommentForm with comment " + newCommentMessage + " on note " + noteOnEdit);
        Concept concept = noteOnEdit.getConcept(page.isSlsMode());
        Set<NoteComment> conceptComments = concept.getComments();
        if (newCommentMessage != null) {
            conceptComments.add(noteRepository.createComment(concept, newCommentMessage));
            newCommentMessage = null;
        }
        comments = getSortedComments(conceptComments);
        System.err.println("onSuccessFromCommentForm --");
        return commentsBlock;
    }

}
