package com.mysema.edith.web;

public interface Service<Type> {
    
    Type getById(Long id);
    
    Type update(Type entity);
    
    Type add(Type entity);
    
    void delete(Long id);

}
