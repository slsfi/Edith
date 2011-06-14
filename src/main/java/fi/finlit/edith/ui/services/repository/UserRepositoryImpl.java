/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.repository;

import static fi.finlit.edith.qtype.QUser.user;
import static fi.finlit.edith.qtype.QUserInfo.userInfo;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.UserDao;

public class UserRepositoryImpl extends AbstractRepository<User> implements UserDao {
    
    private final AuthService authService;

    public UserRepositoryImpl(@Inject SessionFactory sessionFactory, @Inject AuthService authService) {
        super(sessionFactory, user);
        this.authService = authService;
    }

    @Override
    public User getByUsername(String username) {
        return getSession().from(user).where(user.username.eq(username)).uniqueResult(user);
    }

    @Override
    public List<User> getOrderedByName() {
        return getSession().from(user).orderBy(user.username.asc()).list(user);
    }

    @Override
    public UserInfo getCurrentUser() {
        return getUserInfoByUsername(authService.getUsername());
    }

    @Override
    public UserInfo getUserInfoByUsername(String username) {
        return getSession().from(userInfo).where(userInfo.username.eq(username)).uniqueResult(userInfo);
    }

    @Override
    public User save(User entity) {
        getSession().save(entity);
        return entity;
    }

    @Override
    public Collection<UserInfo> getAllUserInfos() {
        return getSession().from(userInfo).list(userInfo);
    }

}
