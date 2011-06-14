package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Identifiable;


/**
 * QIdentifiable is a Querydsl query type for Identifiable
 */
public class QIdentifiable extends EntityPathBase<Identifiable> {

    private static final long serialVersionUID = 321942553;

    public static final QIdentifiable identifiable = new QIdentifiable("identifiable");

    public final StringPath id = createString("id");

    public QIdentifiable(String variable) {
        super(Identifiable.class, forVariable(variable));
    }

    public QIdentifiable(BeanPath<? extends Identifiable> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QIdentifiable(PathMetadata<?> metadata) {
        super(Identifiable.class, metadata);
    }

}

