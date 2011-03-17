package fi.finlit.edith.ui.services.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.TaskExecution;

@EagerLoad
public class TasksService implements Runnable {

    private final SessionFactory sessionFactory;

    private final List<Task> tasks = new ArrayList<Task>();

    public TasksService(
            @Inject SessionFactory sessionFactory,
            @Inject ReplacedByAdditionTask replacedBy) {
        this.sessionFactory = sessionFactory;
        tasks.add(replacedBy);
    }

    @Override
    public void run() {
        Session session = sessionFactory.openSession();

        try{
            for (Task task : tasks){
                TaskExecution execution = session.get(TaskExecution.class, task.getId());
                if (execution == null){
                    task.run();
                    execution = new TaskExecution(task.getId());
                    session.save(execution);
                }
            }
        }finally{
            session.flush();
            session.close();
        }
    }

}
