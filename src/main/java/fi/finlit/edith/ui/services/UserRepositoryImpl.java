/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QUser.user;

import java.util.List;

import com.mysema.rdfbean.dao.AbstractRepository;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.domain.UserRepository;

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
