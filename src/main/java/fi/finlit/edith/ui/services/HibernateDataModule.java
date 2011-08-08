/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateDataModule {
    private HibernateDataModule() {
    }

    private static final Logger logger = LoggerFactory.getLogger(HibernateDataModule.class);

    @Startup
    public static void initData(UserDao userDao)
            throws IOException {
        logger.info("Creating users");
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
    }

}
