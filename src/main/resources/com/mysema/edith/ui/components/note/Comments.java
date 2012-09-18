package com.mysema.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.dto.NoteCommentComparator;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.ui.pages.document.Annotate;
import com.sun.xml.internal.ws.api.PropertySet.Property;

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
        Set<NoteComment> conceptComments = noteOnEdit.getComments();
        if (newCommentMessage != null) {
            conceptComments.add(noteDao.createComment(noteOnEdit, newCommentMessage));
            newCommentMessage = null;
        }
        comments = getSortedComments(conceptComments);
        return commentsBlock;
    }

}
