/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.mysema.edith.Identifiable;
import com.mysema.edith.dto.NoteSearchTO;

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

    protected static NoteSearchTO normalize(NoteSearchTO search) {
        if (search.getPerPage() == null) {
            search.setPerPage(25L);
        } else if (search.getPerPage() <= 0) {
            search.setPerPage(Long.valueOf(Integer.MAX_VALUE));
        }
        if (search.getPage() == null) {
            search.setPage(1L);
        }
        return search;
    }

    protected static final long totalPages(long pageSize, long count) {
        if (count == 0) {
            return 1;
        } else if (count % pageSize != 0) {
            return count / pageSize + 1;
        }
        return count / pageSize;
    }

    protected static final Response NOT_FOUND = Response.status(Status.NOT_FOUND).build();

}
