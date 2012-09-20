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

    final String name;

    private Map<String, AtomicInteger> counts = new HashMap<String, AtomicInteger>();

    Item(String name) {
        this.name = name;
    }

    public String getName(String elemName) {
        AtomicInteger intValue = counts.get(elemName);
        if (intValue == null) {
            intValue = new AtomicInteger(1);
            counts.put(elemName, intValue);
            return elemName;
        }
        intValue.addAndGet(1);
        return elemName + intValue;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Item item = (Item) super.clone();
        item.counts = new HashMap<String, AtomicInteger>();
        for (String key : counts.keySet()) {
            item.counts.put(key, counts.get(key));
        }

        return item;
    }

}