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

/**
 * UserInfo provides
 *
 * @author tiwe
 * @version $Id$
 */
//TODO : use "user:"<username> as URL schema ?!?
@ClassMapping(ns=EDITH.NS, ln="User")
public class UserInfo extends Identifiable {

    @Predicate
    @Unique
    private String username;

    public UserInfo() {
    }

    public UserInfo(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserInfo [username=" + username + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (username == null ? 0 : username.hashCode());
        return result;
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
        UserInfo other = (UserInfo) obj;
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }


}
