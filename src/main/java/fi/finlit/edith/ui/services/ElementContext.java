/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.Nullable;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * ElementContext provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ElementContext implements Cloneable {

    public static class Item implements Cloneable {

        private final String name;

        private Map<String,MutableInt> counts = new HashMap<String,MutableInt>();

        Item(String name){
            this.name = name;
        }

        public String getName(String name) {
            MutableInt intValue = counts.get(name);
            if (intValue == null){
                intValue = new MutableInt(1);
                counts.put(name, intValue);
                return name;
            }
            intValue.add(1);
            return name + intValue;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public Object clone() throws CloneNotSupportedException{
            Item item = (Item) super.clone();
            item.counts = new HashMap<String, MutableInt>();
            for (String key : counts.keySet()) {
                item.counts.put(key, counts.get(key));
            }

            return item;
        }

    }

    private Stack<Item> stack = new Stack<Item>();

    private final int offset;

    public ElementContext(int offset){
        this.offset = offset;
    }

    public void push(String name){
        String s = name;
        if (!stack.isEmpty()){
            s = stack.peek().getName(s);
        }
        stack.push(new Item(s));
    }

    public void pop(){
        stack.pop();
    }

    @Override
    public String toString() {
        return this.stack.toString();
    }

    @Nullable
    public String getPath(){
        if (stack.size() > offset){
            StringBuilder b = new StringBuilder();
            for (int i = offset; i < stack.size(); i++){
                if (i > offset) {
                    b.append("-");
                }
                b.append(stack.get(i).name);
            }
            return b.toString();
        }
        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ElementContext clone = (ElementContext) super.clone();
        clone.stack = new Stack<Item>();

        for (Item item : stack) {
            clone.stack.push((Item) item.clone());
        }

        return clone;
    }

    public boolean equalsAny(String... strings) {
        String path = getPath();
        for (String s : strings) {
            if (s.equals(path)) {
                return true;
            }
        }
        return false;
    }

}
