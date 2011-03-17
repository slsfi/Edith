package fi.finlit.edith.ui.services.tasks;

import com.mysema.rdfbean.model.UID;

public interface Task extends Runnable{

    UID getId();

}
