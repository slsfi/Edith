/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;

import fi.finlit.edith.domain.Identifiable;

public class PrimaryKeyEncoder<T extends Identifiable> implements ValueEncoder<T> {

    private final Map<String, T> keyToValue = new HashMap<String, T>();

    private final Repository<T,String> repository;

    public PrimaryKeyEncoder( Repository<T,String> repository) {
        this.repository = repository;
    }

    public final List<T> getAllValues() {
        List<T> result = CollectionFactory.newList();
        for (Map.Entry<String, T> entry : keyToValue.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public String toClient(T value) {
        return value.getId();
    }

    @Override
    public T toValue(String id) {
        T rv = repository.getById(id);
        keyToValue.put(id, rv);
        return rv;
    }
}