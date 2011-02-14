/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.annotations.Unique;

import fi.finlit.edith.EDITH;

@ClassMapping(ns=EDITH.NS)
public class Document extends Identifiable implements Comparable<Document>{

    @Predicate
    private String description;

    @Predicate
    @Unique
    private String svnPath;

    @Predicate
    private String title;

    @Override
    public int compareTo(Document doc) {
        return svnPath.compareTo(doc.svnPath);
    }

    public String getDescription() {
        return description;
    }

    public String getSvnPath() {
        return svnPath;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + (svnPath == null ? 0 : svnPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Document other = (Document) obj;
        if (svnPath == null) {
            if (other.svnPath != null) {
                return false;
            }
        } else if (!svnPath.equals(other.svnPath)) {
            return false;
        }
        return true;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSvnPath(String svnPath) {
        this.svnPath = svnPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentRevision getRevision(long revision){
        return new DocumentRevision(this, revision);
    }

}
