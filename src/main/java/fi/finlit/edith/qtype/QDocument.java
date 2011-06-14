package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Document;


/**
 * QDocument is a Querydsl query type for Document
 */
public class QDocument extends EntityPathBase<Document> {

    private static final long serialVersionUID = -939886498;

    public static final QDocument document = new QDocument("document");

    public final QIdentifiable _super = new QIdentifiable(this);

    public final StringPath description = createString("description");

    //inherited
    public final StringPath id = _super.id;

    public final StringPath svnPath = createString("svnPath");

    public final StringPath title = createString("title");

    public QDocument(String variable) {
        super(Document.class, forVariable(variable));
    }

    public QDocument(BeanPath<? extends Document> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QDocument(PathMetadata<?> metadata) {
        super(Document.class, metadata);
    }

}

