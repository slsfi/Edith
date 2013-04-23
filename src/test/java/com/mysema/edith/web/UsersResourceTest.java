package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.UserTO;
import com.mysema.edith.services.UserDao;

public class UsersResourceTest extends AbstractResourceTest {
    
    @Inject
    private UserDao userDao;
    
    @Inject
    private UsersResource users;
    
    @Before
    public void setUp() throws IOException {
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
    }
    
    @Test
    public void GetById() {
        User user = userDao.getByUsername("timo");
        assertNotNull(users.getById(user.getId()));
    }
    
    @Test
    public void Add() {
        UserTO user = new UserTO();
        user.setUsername("test"+System.currentTimeMillis());
        users.create(user);
    }

}
