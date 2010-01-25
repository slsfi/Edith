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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
}
