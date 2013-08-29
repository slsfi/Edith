/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "term")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Term extends BaseEntity {

    private String basicForm;

    private String meaning;

    @Enumerated(EnumType.STRING)
    private TermLanguage language;

    private String otherLanguage;

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getBasicForm() {
        return basicForm;
    }

    public void setOtherLanguage(String otherLanguage) {
        this.otherLanguage = otherLanguage;
    }

    public void setLanguage(TermLanguage language) {
        this.language = language;
    }

    public String getOtherLanguage() {
        return otherLanguage;
    }

    public TermLanguage getLanguage() {
        return language;
    }
}
