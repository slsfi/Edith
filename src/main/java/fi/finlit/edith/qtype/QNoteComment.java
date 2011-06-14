package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NoteComment;


/**
 * QNoteComment is a Querydsl query type for NoteComment
 */
public class QNoteComment extends EntityPathBase<NoteComment> {

    private static final long serialVersionUID = 1126158314;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QNoteComment noteComment = new QNoteComment("noteComment");

    public final QIdentifiable _super = new QIdentifiable(this);

    protected QConcept concept;

    public final DateTimePath<org.joda.time.DateTime> createdAt = createDateTime("createdAt", org.joda.time.DateTime.class);

    //inherited
    public final StringPath id = _super.id;

    public final StringPath message = createString("message");

    public final StringPath username = createString("username");

    public QNoteComment(String variable) {
        this(NoteComment.class, forVariable(variable), INITS);
    }

    public QNoteComment(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QNoteComment(PathMetadata<?> metadata, PathInits inits) {
        this(NoteComment.class, metadata, inits);
    }

    public QNoteComment(Class<? extends NoteComment> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concept = inits.isInitialized("concept") ? new QConcept(forProperty("concept"), inits.get("concept")) : null;
    }

    public QConcept concept() {
        if (concept == null){
            concept = new QConcept(forProperty("concept"));
        }
        return concept;
    }

}

