/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

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

}
