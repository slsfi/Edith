package com.mysema.edith.services;

public class SubversionException extends RuntimeException {
    private static final long serialVersionUID = 2137588590021188211L;

    public SubversionException() {
        // TODO Auto-generated constructor stub
    }

    public SubversionException(Throwable t) {
        super(t);
    }

    public SubversionException(String msg, Throwable t) {
        super(msg, t);
    }
}
