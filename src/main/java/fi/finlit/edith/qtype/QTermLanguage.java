package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.TermLanguage;


/**
 * QTermLanguage is a Querydsl query type for TermLanguage
 */
public class QTermLanguage extends EnumPath<TermLanguage> {

    private static final long serialVersionUID = -77570649;

    public static final QTermLanguage termLanguage = new QTermLanguage("termLanguage");

    public QTermLanguage(String variable) {
        super(TermLanguage.class, forVariable(variable));
    }

    public QTermLanguage(BeanPath<TermLanguage> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QTermLanguage(PathMetadata<?> metadata) {
        super(TermLanguage.class, metadata);
    }

}

