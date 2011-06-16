/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.dto.UserDetailsImpl;
import fi.finlit.edith.ui.services.UserDao;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Inject
    private final UserDao userDao;

    public UserDetailsServiceImpl(UserDao userRepository) {
        this.userDao = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userDao.getByUsername(username);
        if (user != null) {
            return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getProfile()
                    .getAuthorities());
        }
        throw new UsernameNotFoundException("User " + username + " not found");
    }

}
