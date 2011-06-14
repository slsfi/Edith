package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Term;


/**
 * QTerm is a Querydsl query type for Term
 */
public class QTerm extends EntityPathBase<Term> {

    private static final long serialVersionUID = 1089996079;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QTerm term = new QTerm("term");

    public final QIdentifiable _super = new QIdentifiable(this);

    public final StringPath basicForm = createString("basicForm");

    protected QConcept concept;

    //inherited
    public final StringPath id = _super.id;

    // custom
    public final QTermLanguage language = new QTermLanguage(forProperty("language"));

    public final StringPath meaning = createString("meaning");

    public final StringPath otherLanguage = createString("otherLanguage");

    public QTerm(String variable) {
        this(Term.class, forVariable(variable), INITS);
    }

    public QTerm(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTerm(PathMetadata<?> metadata, PathInits inits) {
        this(Term.class, metadata, inits);
    }

    public QTerm(Class<? extends Term> type, PathMetadata<?> metadata, PathInits inits) {
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

