package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NameForm;


/**
 * QNameForm is a Querydsl query type for NameForm
 */
public class QNameForm extends EntityPathBase<NameForm> {

    private static final long serialVersionUID = 38561074;

    public static final QNameForm nameForm = new QNameForm("nameForm");

    public final QIdentifiable _super = new QIdentifiable(this);

    public final StringPath description = createString("description");

    public final StringPath first = createString("first");

    //inherited
    public final StringPath id = _super.id;

    public final StringPath last = createString("last");

    public QNameForm(String variable) {
        super(NameForm.class, forVariable(variable));
    }

    public QNameForm(BeanPath<? extends NameForm> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QNameForm(PathMetadata<?> metadata) {
        super(NameForm.class, metadata);
    }

}

