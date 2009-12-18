/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.ui.services;

import java.util.List;

import static fi.finlit.editio.domain.QUser.user;

import fi.finlit.editio.domain.User;
import fi.finlit.editio.domain.UserRepository;

/**
 * UserServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class UserRepositoryImpl extends AbstractRepository<User> implements UserRepository{
    
    public UserRepositoryImpl() {
        super(user);
    }

    @Override
    public User getByUsername(String username){
        return getSession().from(user).where(user.username.eq(username))
            .uniqueResult(user);
    }
    
    @Override
    public List<User> getOrderedByName() {
        return getSession().from(user).orderBy(user.username.asc())
            .list(user);
    }

}
