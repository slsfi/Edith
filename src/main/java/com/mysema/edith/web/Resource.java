package com.mysema.edith.web;


public interface Resource<Type> {

    Type getById(Long id);

    Type update(Long id, Type entity);

    Type create(Type entity);

    void delete(Long id);

}
