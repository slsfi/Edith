/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.domain;

import java.util.List;


/**
 * UserRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
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
