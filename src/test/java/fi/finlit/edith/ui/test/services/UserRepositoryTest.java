/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.UserDao;

public class UserRepositoryTest extends AbstractServiceTest {

    @Inject
    private UserDao userRepository;

    @Inject
    private AuthService authService;

    @Test
    public void GetByUsername() {
        for (String username : Arrays.asList("timo", "lassi", "sakari", "ossi")) {
            assertNotNull(userRepository.getByUsername(username));
        }
    }

    @Test
    public void GetCurrentUser() {
        assertEquals(authService.getUsername(), userRepository.getCurrentUser().getUsername());
    }

    @Test
    public void GetOrderedByName() {
        List<User> users = userRepository.getOrderedByName();
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
            assertNotNull(userRepository.getUserInfoByUsername(username));
        }
    }
}
