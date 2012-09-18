/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import com.mysema.edith.domain.Profile;
import com.mysema.edith.domain.User;
import com.mysema.edith.services.UserDao;

public class Register extends Base {

    private User user;

    @Inject
    private UserDao userDao;

    @InjectPage
    private Login loginPage;

    public Object onSuccess() {
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
