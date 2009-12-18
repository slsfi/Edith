/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


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
    User getByUsername(String shortName);
    
    /**
     * @return
     */
    List<User> getOrderedByName();

}
