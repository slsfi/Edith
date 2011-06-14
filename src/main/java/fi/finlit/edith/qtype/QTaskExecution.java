package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.TaskExecution;


/**
 * QTaskExecution is a Querydsl query type for TaskExecution
 */
public class QTaskExecution extends EntityPathBase<TaskExecution> {

    private static final long serialVersionUID = 1655686576;

    public static final QTaskExecution taskExecution = new QTaskExecution("taskExecution");

    public final SimplePath<com.mysema.rdfbean.model.ID> id = createSimple("id", com.mysema.rdfbean.model.ID.class);

    public QTaskExecution(String variable) {
        super(TaskExecution.class, forVariable(variable));
    }

    public QTaskExecution(BeanPath<? extends TaskExecution> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QTaskExecution(PathMetadata<?> metadata) {
        super(TaskExecution.class, metadata);
    }

}

