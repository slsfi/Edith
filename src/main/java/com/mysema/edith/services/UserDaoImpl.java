/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Profile;
import com.mysema.edith.domain.QUser;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.UserInfo;
import com.mysema.query.types.ConstructorExpression;

@Transactional
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

    private static final QUser user = QUser.user;
    
    private final AuthService authService;
    
//    private final SaltSource saltSource;
//
//    private final PasswordEncoder passwordEncoder;

//    @Inject
//    public UserDaoImpl(AuthService authService, SaltSource saltSource,
//            PasswordEncoder passwordEncoder) {
//        this.authService = authService;
//        this.saltSource = saltSource;
//        this.passwordEncoder = passwordEncoder;
//    }
    
    @Inject
    public UserDaoImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public User getById(Long id) {
        return query().from(user).where(user.id.eq(id)).uniqueResult(user);
    }

    @Override
    public Collection<User> getAll() {
        return query().from(user).where(user.active.eq(true)).list(user);
    }

    @Override
    public User getByUsername(String username) {
        return query().from(user).where(user.username.eq(username)).uniqueResult(user);
    }

    @Override
    public User getCurrentUser() {
        return getByUsername(authService.getUsername());
    }

    @Override
    public Collection<UserInfo> getAllUserInfos() {
        return query().from(user).where(user.active.eq(true))
                .list(ConstructorExpression.create(UserInfo.class, user.id, user.username));
    }

    @Override
    public List<User> addUsersFromCsvFile(String filePath, String encoding) throws IOException {

        // "/users.csv"), "ISO-8859-1"
        List<String> lines = Resources.readLines(UserDaoImpl.class.getResource(filePath), Charset.forName(encoding));
        List<User> users = new ArrayList<User>();
        for (String line : lines) {
            String[] values = line.split(";");
            User user = getByUsername(values[2]);
            if (user == null) {
                user = new User();
            }
            user.setActive(true);
            user.setFirstName(values[0]);
            user.setLastName(values[1]);
            user.setUsername(values[2]);
            user.setEmail(values[3]);
            if (values[3].endsWith("mysema.com")) {
                user.setProfile(Profile.Admin);
            } else {
                user.setProfile(Profile.User);
            }

            // encode password
//            UserDetailsImpl userDetails = new UserDetailsImpl(user.getUsername(),
//                    user.getPassword(), user.getProfile().getAuthorities());
//            String password = passwordEncoder.encodePassword(user.getUsername(),
//                    saltSource.getSalt(userDetails));
//            user.setPassword(password);

            persist(user);
            users.add(user);
        }
        return users;
    }

    @Override
    public void save(User user) {
        persist(user);
    }

}
