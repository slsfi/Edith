package fi.finlit.editio.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.editio.EDITIO;

/**
 * User provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public class User {
    
    @Predicate
    private String firstName, lastName, email;
    
    @Predicate
    private String username, password;

    @Predicate
    private Profile profile;
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    
}
