/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.Set;

import com.mysema.edith.domain.NameForm;

public class PlaceTO {
    
    private Long id;
    
    private NameForm normalized;
    
    private Set<NameForm> otherForms;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NameForm getNormalized() {
        return normalized;
    }

    public void setNormalized(NameForm normalized) {
        this.normalized = normalized;
    }

    public Set<NameForm> getOtherForms() {
        return otherForms;
    }

    public void setOtherForms(Set<NameForm> otherForms) {
        this.otherForms = otherForms;
    }
    
}
