/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import fi.finlit.edith.domain.User;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Inject
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.getByUsername(username);
        if (user != null) {
            return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getProfile()
                    .getAuthorities());
        }
        throw new UsernameNotFoundException("User " + username + " not found");
    }

}
