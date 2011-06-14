package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NoteStatus;


/**
 * QNoteStatus is a Querydsl query type for NoteStatus
 */
public class QNoteStatus extends EnumPath<NoteStatus> {

    private static final long serialVersionUID = -1441001209;

    public static final QNoteStatus noteStatus = new QNoteStatus("noteStatus");

    public QNoteStatus(String variable) {
        super(NoteStatus.class, forVariable(variable));
    }

    public QNoteStatus(BeanPath<NoteStatus> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QNoteStatus(PathMetadata<?> metadata) {
        super(NoteStatus.class, metadata);
    }

}

