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
            
            if (source == null) {
                return null;
            // id to bean
            } else if (source instanceof Long && Identifiable.class.isAssignableFrom(targetClass)) {
                return em.get().find(targetClass, source);
            // bean to id
            } else if (source instanceof Identifiable && targetClass.equals(Long.class)) {
                return (T) ((Identifiable)source).getId();
            } else if (targetClass.isInstance(source)) {
                return (T) source;
            } else if (targetClass.isEnum()) {    
                return (T) Enum.valueOf((Class)targetClass, source.toString());
            } else {
                return convert(source, targetClass.newInstance());
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
            if (source instanceof Map) {
                return (T) convertBean((Map)source, new BeanMap(target));
            } else if (source != null) {
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
    private Object convertBean(Map<String, Object> source, BeanMap target) throws InstantiationException, IllegalAccessException {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Class<?> targetType = target.getType(entry.getKey());
            if (targetType != null && target.getWriteMethod(entry.getKey()) != null) {
                boolean primitive = target.getType(entry.getKey()).isPrimitive();
                if (entry.getKey().equals("class") || (primitive && entry.getValue() == null)) {
                    continue;
                }    
            } else {
                continue;
            }
                        
            Object sourceValue = source.get(entry.getKey());            
            Class<?> sourceType = sourceValue != null ? sourceValue.getClass() : Object.class;
            Object targetValue = null;
            if (sourceValue == null) {
                // do nothing
            // collection
            } else if (Collection.class.isAssignableFrom(targetType) && Collection.class.isAssignableFrom(sourceType)) {
                Collection sourceColl = (Collection)sourceValue;
                Collection targetColl = (Collection) containerTypes.get(targetType).newInstance();
                targetValue = targetColl;
                if (sourceColl != null && !sourceColl.isEmpty()) {
                    Type genericType = target.getReadMethod(entry.getKey()).getGenericReturnType();
                    Class elementType = ReflectionUtils.getTypeParameterAsClass(genericType, 0);
                    if (!elementType.isInstance(sourceColl.iterator().next())) {
                        for (Object obj : sourceColl) {
                            targetColl.add(convert(obj, elementType));
                        }       
                    } else {
                        targetColl.addAll(sourceColl);
                    }
                }
            // direct
            } else if (targetType.isAssignableFrom(sourceType)) {
                targetValue = sourceValue;
            // other
            } else { 
                targetValue = convert(sourceValue, targetType);
            }
            target.put(entry.getKey(), targetValue);
        }
        
        return target.getBean();

    }
    
}
