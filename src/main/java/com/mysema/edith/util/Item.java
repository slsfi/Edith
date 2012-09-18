package com.mysema.edith.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

public class Item implements Cloneable {

    final String name;

    private Map<String, MutableInt> counts = new HashMap<String, MutableInt>();

    Item(String name) {
        this.name = name;
    }

    public String getName(String elemName) {
        MutableInt intValue = counts.get(elemName);
        if (intValue == null) {
            intValue = new MutableInt(1);
            counts.put(elemName, intValue);
            return elemName;
        }
        intValue.add(1);
        return elemName + intValue;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Item item = (Item) super.clone();
        item.counts = new HashMap<String, MutableInt>();
        for (String key : counts.keySet()) {
            item.counts.put(key, counts.get(key));
        }

        return item;
    }

}