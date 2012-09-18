/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.util;

import java.util.Stack;


public class ElementContext implements Cloneable {

    private Stack<Item> stack = new Stack<Item>();

    private final int offset;

    private String path = null;

    public ElementContext(int offset) {
        this.offset = offset;
    }

    public void push(String name) {
        String s = name;
        if (!stack.isEmpty()) {
            s = stack.peek().getName(s);
        }
        stack.push(new Item(s));
        path = null;
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
        if (stack.size() > offset) {
            StringBuilder b = new StringBuilder();
            for (int i = offset; i < stack.size(); i++) {
                if (i > offset) {
                    b.append("-");
                }
                b.append(stack.get(i).name);
            }
            path = b.toString();
            return path;
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
        for (String s : strings) {
            if (s.equals(getPath())) {
                return true;
            }
        }
        return false;
    }

}
