/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.mysema.edith.Identifiable;

public abstract class AbstractResource {

    @Inject
    private Converter converter;

    protected <F, T> T convert(F source, Class<T> target) {
        return converter.convert(source, target);
    }
    
    protected <F, T extends Identifiable> T convert(F source, T target) {
        return converter.convert(source, target);
    }

    protected <F, T> List<T> convert(List<F> source, Class<T> targetClass) {
        try {
            List<T> result = new ArrayList<T>(source.size());
            for (F obj : source) {
                result.add(converter.convert(obj, targetClass.newInstance()));
            }
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected static final long totalPages(long pageSize, long count) {
        if (count == 0) {
            return 1;
        } else if (count % pageSize != 0) {
            return count / pageSize + 1;
        }
        return count / pageSize;
    }

}
