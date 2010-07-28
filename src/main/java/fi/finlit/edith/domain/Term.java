/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Term provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS)
public class Term extends Identifiable {
    
    @Predicate
    private String basicForm;

    @Predicate
    private String meaning;

    @Predicate
    private TermLanguage language;

    public String getBasicForm() {
        return basicForm;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public TermLanguage getLanguage() {
        return language;
    }

    public void setLanguage(TermLanguage language) {
        this.language = language;
    }

    public Term createCopy() {
        Term term = new Term();
        term.setBasicForm(basicForm);
        term.setMeaning(meaning);
        term.setLanguage(language);
        return term;
    }

}
