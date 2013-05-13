/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

public class VersioningException extends RuntimeException {
    private static final long serialVersionUID = 2137588590021188211L;

    public VersioningException() {}

    public VersioningException(Throwable t) {
        super(t);
    }

    public VersioningException(String msg, Throwable t) {
        super(msg, t);
    }
}
