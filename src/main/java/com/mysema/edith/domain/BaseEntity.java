package com.mysema.edith.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.mysema.edith.Identifiable;

@MappedSuperclass
public abstract class BaseEntity implements Identifiable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    private Long id;
        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
