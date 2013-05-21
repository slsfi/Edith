/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.util;

import java.util.Stack;

public class ElementContext implements Cloneable {

    private Stack<Item> stack = new Stack<Item>();

    private String path = null;

    public void push(String name) {
        String s = name;
        if (!stack.isEmpty()) {
            s = stack.peek().getName(s);
        }
        stack.push(new Item(s));
        path = null;
    }
    
    public String peek() {
        return stack.peek().toString();
    } 

    public void pop() {
        stack.pop();
        path = null;
    }

    @Override
    public String toString() {
        return stack.toString();
    }

    public String getPath() {
        if (path != null) {
            return path;
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            if (i > 0) {
                b.append("-");
            }
            b.append(stack.get(i).getName());
        }
        path = b.toString();
        return path;
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
        for (String s : strings) {
            if (s.equals(getPath())) {
                return true;
            }
        }
        return false;
    }

}
