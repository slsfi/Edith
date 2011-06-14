package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.UserInfo;


/**
 * QConcept is a Querydsl query type for Concept
 */
public class QConcept extends EntityPathBase<Concept> {

    private static final long serialVersionUID = 200171205;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QConcept concept = new QConcept("concept");

    public final QIdentifiable _super = new QIdentifiable(this);

    public final SetPath<UserInfo, QUserInfo> allEditors = this.<UserInfo, QUserInfo>createSet("allEditors", UserInfo.class, QUserInfo.class);

    public final SetPath<NoteComment, QNoteComment> comments = this.<NoteComment, QNoteComment>createSet("comments", NoteComment.class, QNoteComment.class);

    public final StringPath description = createString("description");

    //inherited
    public final StringPath id = _super.id;

    protected QUserInfo lastEditedBy;

    public final StringPath sources = createString("sources");

    // custom
    public final QNoteStatus status = new QNoteStatus(forProperty("status"));

    public final StringPath subtextSources = createString("subtextSources");

    public final SetPath<NoteType, QNoteType> types = this.<NoteType, QNoteType>createSet("types", NoteType.class, QNoteType.class);

    public QConcept(String variable) {
        this(Concept.class, forVariable(variable), INITS);
    }

    public QConcept(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QConcept(PathMetadata<?> metadata, PathInits inits) {
        this(Concept.class, metadata, inits);
    }

    public QConcept(Class<? extends Concept> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lastEditedBy = inits.isInitialized("lastEditedBy") ? new QUserInfo(forProperty("lastEditedBy")) : null;
    }

    public QUserInfo lastEditedBy() {
        if (lastEditedBy == null){
            lastEditedBy = new QUserInfo(forProperty("lastEditedBy"));
        }
        return lastEditedBy;
    }

}

