package fi.finlit.edith.sql.domain;

import javax.persistence.Column;

public class User extends Identifiable{

    private String firstName, lastName, email;

    private String password;

    private Profile profile;

    @Column(unique = true)
    private String username;

    public User() {

    }

    public User(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
