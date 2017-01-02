/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.IOException;
import java.util.List;

import com.mysema.edith.domain.User;

/**
 * @author tiwe
 *
 */
public interface UserDao extends Dao<User, Long> {

    /**
     * Get the user with the given username
     *
     * @param shortName
     * @return
     */
    User getByUsername(String shortName);

    /**
     * Get the current user
     *
     * @param username
     * @return
     */
    User getCurrentUser();

    /**
     * @param filePath
     * @param encoding
     * @return
     * @throws IOException
     */
    List<User> addUsersFromCsvFile(String filePath, String encoding) throws IOException;

    /**
     * @param user
     */
    User save(User user);

    /**
     * @return
     */
    List<User> getAll();

}
