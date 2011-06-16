/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import fi.finlit.edith.dto.UserInfo;
import fi.finlit.edith.sql.domain.User;

public interface UserDao extends Dao<User, Long> {

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
     * 
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

    /**
     * @return
     */
    Collection<UserInfo> getAllUserInfos();

    @CommitAfter
    List<User> addUsersFromCsvFile(String filePath, String encoding) throws IOException;
    
    @CommitAfter
    void save(User user);

}
