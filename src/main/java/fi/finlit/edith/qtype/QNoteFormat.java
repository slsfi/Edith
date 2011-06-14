package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.NoteFormat;


/**
 * QNoteFormat is a Querydsl query type for NoteFormat
 */
public class QNoteFormat extends EnumPath<NoteFormat> {

    private static final long serialVersionUID = -1817298676;

    public static final QNoteFormat noteFormat = new QNoteFormat("noteFormat");

    public QNoteFormat(String variable) {
        super(NoteFormat.class, forVariable(variable));
    }

    public QNoteFormat(BeanPath<NoteFormat> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QNoteFormat(PathMetadata<?> metadata) {
        super(NoteFormat.class, metadata);
    }

}

