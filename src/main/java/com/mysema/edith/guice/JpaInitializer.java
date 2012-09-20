/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.guice;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class JpaInitializer {
    @Inject
    public JpaInitializer(PersistService service) {
        service.start();
    }
}