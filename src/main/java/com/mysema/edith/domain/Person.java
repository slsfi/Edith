/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "person")
public class Person extends BaseEntity {
    
    @Embedded
    private NameForm normalized;

    @ElementCollection
    @CollectionTable(name = "person_nameform", joinColumns = @JoinColumn(name = "person_id"))
    private Set<NameForm> otherForms = new HashSet<NameForm>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "time_of_birth_start")),
            @AttributeOverride(name = "end", column = @Column(name = "time_of_birth_end")) })
    private Interval timeOfBirth;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "time_of_death_start")),
            @AttributeOverride(name = "end", column = @Column(name = "time_of_death_end")) })
    private Interval timeOfDeath;

    public Person() {
    }

    public Person(NameForm normalized) {
        this.normalized = normalized;
    }

    public Person(NameForm normalized, Set<NameForm> otherForms) {
        this(normalized);
        this.otherForms = otherForms;
    }

    public NameForm getNormalized() {
        return normalized;
    }

    public Set<NameForm> getOtherForms() {
        return otherForms;
    }

    public Interval getTimeOfBirth() {
        return timeOfBirth;
    }

    public Interval getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setNormalized(NameForm normalized) {
        this.normalized = normalized;
    }

    public void setOtherForms(Set<NameForm> otherForms) {
        this.otherForms = otherForms;
    }

    public void setTimeOfBirth(Interval timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public void setTimeOfDeath(Interval timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

}
