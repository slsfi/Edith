package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Place;


/**
 * QPlace is a Querydsl query type for Place
 */
public class QPlace extends EntityPathBase<Place> {

    private static final long serialVersionUID = -573362012;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QPlace place = new QPlace("place");

    public final QIdentifiable _super = new QIdentifiable(this);

    //inherited
    public final StringPath id = _super.id;

    protected QNameForm normalizedForm;

    public final SetPath<NameForm, QNameForm> otherForms = this.<NameForm, QNameForm>createSet("otherForms", NameForm.class, QNameForm.class);

    public QPlace(String variable) {
        this(Place.class, forVariable(variable), INITS);
    }

    public QPlace(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlace(PathMetadata<?> metadata, PathInits inits) {
        this(Place.class, metadata, inits);
    }

    public QPlace(Class<? extends Place> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.normalizedForm = inits.isInitialized("normalizedForm") ? new QNameForm(forProperty("normalizedForm")) : null;
    }

    public QNameForm normalizedForm() {
        if (normalizedForm == null){
            normalizedForm = new QNameForm(forProperty("normalizedForm"));
        }
        return normalizedForm;
    }

}

