/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.UserDao;

public class UserDaoTest extends AbstractHibernateTest {

    @Inject
    private UserDao userDao;

    @Inject
    private AuthService authService;

    @Before
    public void before() throws IOException {
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
    }

    @Test
    public void GetByUsername() {
        for (String username : Arrays.asList("timo", "lassi", "sakari", "ossi")) {
            assertNotNull(userDao.getByUsername(username));
        }
    }

    @Test
    public void GetCurrentUser() {
        assertEquals(authService.getUsername(), userDao.getCurrentUser().getUsername());
    }

    @Test
    public void GetOrderedByName() {
        List<User> users = userDao.getOrderedByName();
        User previous = null;
        for (User user : users) {
            if (previous != null) {
                assertTrue(previous.getUsername().compareTo(user.getUsername()) <= 0);
            }
            previous = user;
        }
    }

    @Test
    public void GetUserInfoByUsername() {
        for (String username : Arrays.asList("timo", "lassi", "sakari", "ossi")) {
            assertNotNull(userDao.getUserInfoByUsername(username));
        }
    }
}
