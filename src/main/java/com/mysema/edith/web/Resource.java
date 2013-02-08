/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

public interface Resource<Type> {
    
    Type getById(Long id);
    
    Type update(Type entity);
    
    Type add(Type entity);
    
    void delete(Long id);

}
