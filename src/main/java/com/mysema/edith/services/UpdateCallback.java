/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author tiwe
 *
 */
public interface UpdateCallback {

    /**
     * @param source
     * @param target
     */
    void update(InputStream source, OutputStream target) throws IOException;

}
