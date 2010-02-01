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
     * @param shortName
     * @return
     */
    @Nullable
    User getByUsername(String shortName);
    
    /**
     * @param username
     * @return
     */
    @Nullable
    UserInfo getCurrentUser();

    /**
     * @return
     */
    List<User> getOrderedByName();

}
