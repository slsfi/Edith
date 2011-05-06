package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.model.ID;
import com.mysema.rdfbean.model.IDType;

@ClassMapping
public class TaskExecution {

    @Id(IDType.RESOURCE)
    private ID id;

    public TaskExecution(ID id) {
        this.id = id;
    }

    public TaskExecution() {}

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

}
