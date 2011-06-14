package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NoteType;


/**
 * QNoteType is a Querydsl query type for NoteType
 */
public class QNoteType extends EnumPath<NoteType> {

    private static final long serialVersionUID = -220458609;

    public static final QNoteType noteType = new QNoteType("noteType");

    public QNoteType(String variable) {
        super(NoteType.class, forVariable(variable));
    }

    public QNoteType(BeanPath<NoteType> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QNoteType(PathMetadata<?> metadata) {
        super(NoteType.class, metadata);
    }

}

