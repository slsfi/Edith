/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

public abstract class AbstractResource<Type> {

    @Inject
    private Converter converter;

    protected <F, T> T convert(F source, T target) {
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

}
