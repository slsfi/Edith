/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import com.google.inject.Inject;

public abstract class AbstractService<Type> implements Service<Type> {
    
    @Inject
    private Converter converter;
    
    protected <F, T> T convert(F source, T target) {
        return converter.convert(source, target);
    }

}
