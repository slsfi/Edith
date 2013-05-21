/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Item implements Cloneable {

    private final String name;

    private Map<String, AtomicInteger> counts = new HashMap<String, AtomicInteger>();

    Item(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public String getName(String elemName) {
        AtomicInteger intValue = counts.get(elemName);
        if (intValue == null) {
            intValue = new AtomicInteger(0);
            counts.put(elemName, intValue);
        }
        return elemName + intValue.getAndAdd(1);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Item item = null;
        try {
            item = (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        } 
        item.counts = new HashMap<String, AtomicInteger>();
        for (String key : counts.keySet()) {
            item.counts.put(key, counts.get(key));
        }

        return item;
    }

}