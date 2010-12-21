package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.IDType;
import com.mysema.rdfbean.model.RDFS;
import com.mysema.rdfbean.model.UID;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class OntologyConcept {

    @Id(IDType.URI)
    private UID id;
    
    @Predicate(ns=RDFS.NS)
    private String label;

    @Predicate
    private Ontology ontology;
    
    public OntologyConcept() {}
    
    public OntologyConcept(UID id, String label) {
        this.id = id;
        this.label = label;
    }    
    
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

    public Ontology getOntology() {
        return ontology;
    }

    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }
    
}
