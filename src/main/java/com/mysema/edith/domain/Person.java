/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.mysema.edith.Identifiable;

@Entity
@Table(name = "person")
public class Person implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
