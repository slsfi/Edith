/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;


/**
 * UserRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface UserRepository extends Repository<User,String>{

    /**
     * Get the user with the given username
     *
     * @param shortName
     * @return
     */
    @Nullable
    User getByUsername(String shortName);

    /**
     * Get the current user
     *
     * @param username
     * @return
     */
    @Nullable
    UserInfo getCurrentUser();

    /**
     * FIXME TEST AND DOCUMENT
     * @param username
     * @return
     */
    @Nullable
    UserInfo getUserInfoByUsername(String username);

    /**
     * Get the users with asceding username order
     *
     * @return
     */
    List<User> getOrderedByName();

}
