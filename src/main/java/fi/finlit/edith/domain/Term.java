package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Phrase provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITH.NS)
public class Term extends Identifiable{

    @Predicate
    private String basicForm;
    
    @Predicate
    private String meaning;

    public String getBasicForm() {
        return basicForm;
    }

    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    

}
