/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.edith.services.UserDao;

public final class HibernateDataModule {
    private HibernateDataModule() {
    }

    private static final Logger logger = LoggerFactory.getLogger(HibernateDataModule.class);

    @Startup
    public static void initData(UserDao userDao) throws IOException {
        logger.info("Creating users");
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
    }

}
