/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.guice;

import java.io.IOException;

import com.google.inject.Inject;
import com.mysema.edith.services.SubversionService;
import com.mysema.edith.services.UserDao;

public class DataInitService {

    @Inject
    public DataInitService(UserDao userDao, SubversionService subversionService) throws IOException {
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
        subversionService.initialize();
    }
    
}
