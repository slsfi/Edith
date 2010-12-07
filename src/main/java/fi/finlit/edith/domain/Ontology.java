package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.IDType;
import com.mysema.rdfbean.model.RDFS;
import com.mysema.rdfbean.model.UID;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Ontology {
    
    @Id(IDType.URI)
    private UID id;

    @Predicate(ns=RDFS.NS)
    private String label;

    public UID getId() {
        return id;
    }

    public void setId(UID id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    
}
