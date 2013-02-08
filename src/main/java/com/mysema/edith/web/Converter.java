/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mysema.edith.Identifiable;
import com.mysema.util.BeanMap;
import com.mysema.util.ReflectionUtils;

@SuppressWarnings("unchecked")
public class Converter {
    
    private static final Map<Class<?>, Class<?>> containerTypes = Maps.newHashMap();
    
    static {
        containerTypes.put(List.class, ArrayList.class);
        containerTypes.put(Set.class, HashSet.class);
        containerTypes.put(Collection.class, ArrayList.class);
    }
    
    @Inject
    private Provider<EntityManager> em;
    
    /**
     * @param source
     * @param targetClass
     * @return
     */
    public <F, T> T convert(F source, Class<T> targetClass) {
        try {
            if (targetClass.isPrimitive()) {
                targetClass = Primitives.wrap(targetClass);
            }
            
            // id to bean
            if (source instanceof Long && Identifiable.class.isAssignableFrom(targetClass)) {
                return em.get().find(targetClass, source);
            // bean to id
            } else if (source instanceof Identifiable && targetClass.equals(Long.class)) {
                return (T) ((Identifiable)source).getId();
            } else if (targetClass.isInstance(source)) {
                return (T) source;
            } else if (source != null) {
                return (T) convertBean(new BeanMap(source), new BeanMap(targetClass.newInstance()));
            } else {
                return null;
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * @param source
     * @param target
     */
    public <F, T> T convert(F source, T target) {
        try {
            if (source != null) {
                return (T) convertBean(new BeanMap(source), new BeanMap(target));
            } else {
                return null;
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    
    /**
     * @param source
     * @param target
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("rawtypes")
    private Object convertBean(BeanMap source, BeanMap target) throws InstantiationException, IllegalAccessException {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Class<?> type = target.getType(entry.getKey());
            if (type != null) {
                boolean primitive = target.getType(entry.getKey()).isPrimitive();
                if (entry.getKey().equals("class") || (primitive && entry.getValue() == null)) {
                    continue;
                }    
            } else {
                continue;
            }
                        
            Class<?> sourceType = source.getType(entry.getKey());
            Object sourceValue = source.get(entry.getKey());
            Object targetValue = null;
            // collection
            if (Collection.class.isAssignableFrom(type) && Collection.class.isAssignableFrom(sourceType)) {
                Collection sourceColl = (Collection)sourceValue;
                Collection targetColl = (Collection) containerTypes.get(type).newInstance();
                targetValue = targetColl;
                if (!sourceColl.isEmpty()) {
                    Type genericType = target.getReadMethod(entry.getKey()).getGenericReturnType();
                    Class elementType = ReflectionUtils.getTypeParameter(genericType, 0);
                    if (!elementType.isInstance(sourceColl.iterator().next())) {
                        for (Object obj : sourceColl) {
                            targetColl.add(convert(obj, elementType));
                        }       
                    } else {
                        targetColl.addAll(sourceColl);
                    }
                }
            // map
            } else if (Map.class.isAssignableFrom(type)) {
                throw new UnsupportedOperationException();
            // direct
            } else if (sourceType.equals(type)) {
                targetValue = sourceValue;
            // other
            } else { 
                targetValue = convert(sourceValue, type);
            }
            target.put(entry.getKey(), targetValue);
        }
        
        return target.getBean();

    }
    
}
