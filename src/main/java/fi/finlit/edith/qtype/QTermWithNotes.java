package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.TermWithNotes;


/**
 * QTermWithNotes is a Querydsl query type for TermWithNotes
 */
public class QTermWithNotes extends EntityPathBase<TermWithNotes> {

    private static final long serialVersionUID = 1268824364;

    public static final QTermWithNotes termWithNotes = new QTermWithNotes("termWithNotes");

    public final StringPath basicForm = createString("basicForm");

    public final StringPath id = createString("id");

    public final StringPath meaning = createString("meaning");

    public final SetPath<Note, QNote> notes = this.<Note, QNote>createSet("notes", Note.class, QNote.class);

    public QTermWithNotes(String variable) {
        super(TermWithNotes.class, forVariable(variable));
    }

    public QTermWithNotes(BeanPath<? extends TermWithNotes> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QTermWithNotes(PathMetadata<?> metadata) {
        super(TermWithNotes.class, metadata);
    }

}

