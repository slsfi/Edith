package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;
import com.mysema.query.types.expr.*;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.util.QueryDelegates;


/**
 * QDocumentNote is a Querydsl query type for DocumentNote
 */
public class QDocumentNote extends EntityPathBase<DocumentNote> {

    private static final long serialVersionUID = -615498384;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QDocumentNote documentNote = new QDocumentNote("documentNote");

    public final QIdentifiable _super = new QIdentifiable(this);

    public final NumberPath<Long> createdOn = createNumber("createdOn", Long.class);

    public final BooleanPath deleted = createBoolean("deleted");

    protected QDocument document;

    //inherited
    public final StringPath id = _super.id;

    public final StringPath lemmaPosition = createString("lemmaPosition");

    public final StringPath longText = createString("longText");

    protected QNote note;

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final BooleanPath publishable = createBoolean("publishable");

    public final StringPath shortText = createString("shortText");

    public final NumberPath<Long> svnRevision = createNumber("svnRevision", Long.class);

    public QDocumentNote(String variable) {
        this(DocumentNote.class, forVariable(variable), INITS);
    }

    public QDocumentNote(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QDocumentNote(PathMetadata<?> metadata, PathInits inits) {
        this(DocumentNote.class, metadata, inits);
    }

    public QDocumentNote(Class<? extends DocumentNote> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.document = inits.isInitialized("document") ? new QDocument(forProperty("document")) : null;
        this.note = inits.isInitialized("note") ? new QNote(forProperty("note"), inits.get("note")) : null;
    }

    public QConcept concept(Boolean extendedTerm) {
        return QueryDelegates.concept(this, extendedTerm);
    }

    public QDocument document() {
        if (document == null){
            document = new QDocument(forProperty("document"));
        }
        return document;
    }

    public QNote note() {
        if (note == null){
            note = new QNote(forProperty("note"));
        }
        return note;
    }

}

