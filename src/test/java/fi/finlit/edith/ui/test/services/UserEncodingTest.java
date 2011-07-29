/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import nu.localhost.tapestry5.springsecurity.services.internal.SaltSourceImpl;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;

import fi.finlit.edith.dto.UserDetailsImpl;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.UserDao;
import fi.finlit.edith.ui.services.hibernate.AbstractHibernateTest;

public class UserEncodingTest extends AbstractHibernateTest {

    @Inject
    private UserDao userRepository;

    @Test
    @Ignore
    public void Encoding() throws Exception {
        PasswordEncoder passwordEncoder = new ShaPasswordEncoder();

        SaltSourceImpl saltSource = new SaltSourceImpl();
        saltSource.setSystemWideSalt("DEADBEEF");
        saltSource.afterPropertiesSet();

        for (User user : userRepository.getOrderedByName()) {
            UserDetailsImpl userDetails = new UserDetailsImpl(user.getUsername(), user
                    .getPassword(), user.getProfile().getAuthorities());
            passwordEncoder.encodePassword(user.getUsername(), saltSource.getSalt(userDetails));
        }
    }
}
