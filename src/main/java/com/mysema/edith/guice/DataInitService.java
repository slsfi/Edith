/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.guice;

import java.io.IOException;

import com.google.inject.Inject;
import com.mysema.edith.services.UserDao;
import com.mysema.edith.services.VersioningDao;

public class DataInitService {

    @Inject
    public DataInitService(UserDao userDao, VersioningDao versioningDao) throws IOException {
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
        versioningDao.initialize();
    }
    
}
