package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Person;


/**
 * QPerson is a Querydsl query type for Person
 */
public class QPerson extends EntityPathBase<Person> {

    private static final long serialVersionUID = -600295592;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QPerson person = new QPerson("person");

    public final QIdentifiable _super = new QIdentifiable(this);

    //inherited
    public final StringPath id = _super.id;

    protected QNameForm normalizedForm;

    public final SetPath<NameForm, QNameForm> otherForms = this.<NameForm, QNameForm>createSet("otherForms", NameForm.class, QNameForm.class);

    protected QInterval timeOfBirth;

    protected QInterval timeOfDeath;

    public QPerson(String variable) {
        this(Person.class, forVariable(variable), INITS);
    }

    public QPerson(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPerson(PathMetadata<?> metadata, PathInits inits) {
        this(Person.class, metadata, inits);
    }

    public QPerson(Class<? extends Person> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.normalizedForm = inits.isInitialized("normalizedForm") ? new QNameForm(forProperty("normalizedForm")) : null;
        this.timeOfBirth = inits.isInitialized("timeOfBirth") ? new QInterval(forProperty("timeOfBirth")) : null;
        this.timeOfDeath = inits.isInitialized("timeOfDeath") ? new QInterval(forProperty("timeOfDeath")) : null;
    }

    public QNameForm normalizedForm() {
        if (normalizedForm == null){
            normalizedForm = new QNameForm(forProperty("normalizedForm"));
        }
        return normalizedForm;
    }

    public QInterval timeOfBirth() {
        if (timeOfBirth == null){
            timeOfBirth = new QInterval(forProperty("timeOfBirth"));
        }
        return timeOfBirth;
    }

    public QInterval timeOfDeath() {
        if (timeOfDeath == null){
            timeOfDeath = new QInterval(forProperty("timeOfDeath"));
        }
        return timeOfDeath;
    }

}

