/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.dto;

import java.util.Set;

import com.mysema.edith.domain.Interval;
import com.mysema.edith.domain.NameForm;

public class PersonTO {
    
    private Long id;
    
    private NameForm normalized;
    
    private Set<NameForm> otherForms;
    
    private Interval timeOfBirth;
    
    private Interval timeOfDeath;

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

    public Interval getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setTimeOfBirth(Interval timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public Interval getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(Interval timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

    
}
