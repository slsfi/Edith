package com.mysema.edith.services;

import java.util.List;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.domain.QNoteComment;

/**
 * @author tiwe
 *
 */
public class NoteCommentDaoImpl extends AbstractDao<NoteComment> implements NoteCommentDao {
    
    private static final QNoteComment noteComment = QNoteComment.noteComment;

    private final AuthService authService;
    
    @Inject
    public NoteCommentDaoImpl(AuthService authService) {
        this.authService = authService;
    }
    
    @Override
    public List<NoteComment> getOfNote(long noteId) {
        return from(noteComment)
                .where(noteComment.note.id.eq(noteId))
                .list(noteComment);
    }
    
    @Override
    public NoteComment getOneOfNote(long noteId) {
        return from(noteComment)
                .where(noteComment.note.id.eq(noteId))
                .singleResult(noteComment);
    }
        
    @Override
    public NoteComment getById(Long id) {
        return find(NoteComment.class, id);
    }

    @Override
    public NoteComment save(NoteComment comment) {
        comment.setCreatedAt(new DateTime());
        comment.setUsername(authService.getUsername());
       return persistOrMerge(comment);
    }

    @Override
    public void remove(NoteComment comment) {
        super.remove(comment);
    }

    @Override
    public void remove(Long id) {
        NoteComment comment = find(NoteComment.class, id);
        if (comment != null) {
            super.remove(comment);
        }
    }

}
