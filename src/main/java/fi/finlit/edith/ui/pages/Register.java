/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.Profile;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.UserDao;

public class Register extends Base{

    private User user;

    @Inject
    private UserDao userDao;

    @InjectPage
    private Login loginPage;

    public Object onSuccess(){
        user.setProfile(Profile.User);
        userDao.save(user);
        // TODO : check that username is not taken
        return loginPage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
