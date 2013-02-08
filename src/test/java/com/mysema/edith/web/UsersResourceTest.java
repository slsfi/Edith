package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.inject.Inject;
import com.mysema.edith.domain.User;
import com.mysema.edith.services.UserDao;

public class UsersResourceTest extends AbstractResourceTest {
    
    @Inject
    private UserDao userDao;
    
    @Inject
    private UsersResource users;
    
    @Test
    public void GetById() {
        User user = userDao.getByUsername("timo");
        assertNotNull(users.getById(user.getId()));
    }

}
