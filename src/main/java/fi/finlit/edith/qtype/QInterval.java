package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Interval;


/**
 * QInterval is a Querydsl query type for Interval
 */
public class QInterval extends EntityPathBase<Interval> {

    private static final long serialVersionUID = -1231188984;

    public static final QInterval interval = new QInterval("interval");

    public final DateTimePath<org.joda.time.DateTime> end = createDateTime("end", org.joda.time.DateTime.class);

    public final StringPath id = createString("id");

    public final DateTimePath<org.joda.time.DateTime> start = createDateTime("start", org.joda.time.DateTime.class);

    public QInterval(String variable) {
        super(Interval.class, forVariable(variable));
    }

    public QInterval(BeanPath<? extends Interval> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QInterval(PathMetadata<?> metadata) {
        super(Interval.class, metadata);
    }

}

