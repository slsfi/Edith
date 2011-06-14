package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;
import com.mysema.query.types.expr.*;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.util.QueryDelegates;


/**
 * QNote is a Querydsl query type for Note
 */
public class QNote extends EntityPathBase<Note> {

    private static final long serialVersionUID = 1089826997;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QNote note = new QNote("note");

    public final QIdentifiable _super = new QIdentifiable(this);

    protected QConcept concept;

    public final NumberPath<Integer> documentNoteCount = createNumber("documentNoteCount", Integer.class);

    public final NumberPath<Long> editedOn = createNumber("editedOn", Long.class);

    // custom
    public final QNoteFormat format = new QNoteFormat(forProperty("format"));

    //inherited
    public final StringPath id = _super.id;

    public final StringPath lemma = createString("lemma");

    public final StringPath lemmaMeaning = createString("lemmaMeaning");

    protected QPerson person;

    protected QPlace place;

    protected QTerm term;

    public QNote(String variable) {
        this(Note.class, forVariable(variable), INITS);
    }

    public QNote(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QNote(PathMetadata<?> metadata, PathInits inits) {
        this(Note.class, metadata, inits);
    }

    public QNote(Class<? extends Note> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concept = inits.isInitialized("concept") ? new QConcept(forProperty("concept"), inits.get("concept")) : null;
        this.person = inits.isInitialized("person") ? new QPerson(forProperty("person"), inits.get("person")) : null;
        this.place = inits.isInitialized("place") ? new QPlace(forProperty("place"), inits.get("place")) : null;
        this.term = inits.isInitialized("term") ? new QTerm(forProperty("term"), inits.get("term")) : null;
    }

    public QConcept concept(Boolean extendedTerm) {
        return QueryDelegates.concept(this, extendedTerm);
    }

    public QConcept concept() {
        if (concept == null){
            concept = new QConcept(forProperty("concept"));
        }
        return concept;
    }

    public QPerson person() {
        if (person == null){
            person = new QPerson(forProperty("person"));
        }
        return person;
    }

    public QPlace place() {
        if (place == null){
            place = new QPlace(forProperty("place"));
        }
        return place;
    }

    public QTerm term() {
        if (term == null){
            term = new QTerm(forProperty("term"));
        }
        return term;
    }

}

