/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.File;

@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Document extends BaseEntity {

    @Column(unique = true)
    private String path;

    private String title;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        File f = new File(this.getPath());
        return f.getName().toString();
      //  return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
