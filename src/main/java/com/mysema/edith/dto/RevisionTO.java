/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

public class RevisionTO {

    private final long svnRevision;

    private final String created;

    private final String creator;

    public RevisionTO(long svnRevision, String created, String creator) {
        this.svnRevision = svnRevision;
        this.created = created;
        this.creator = creator;
    }

    public RevisionTO(long svnRevision) {
        this.svnRevision = svnRevision;
        this.created = "";
        this.creator = "";
    }

    public long getSvnRevision() {
        return svnRevision;
    }

    public String getCreated() {
        return created;
    }

    public String getCreator() {
        return creator;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(svnRevision).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RevisionTO other = (RevisionTO) obj;
        if (svnRevision != other.svnRevision) {
            return false;
        }
        return true;
    }
}
