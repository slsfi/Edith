package com.mysema.edith.web;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Set;

import com.google.common.collect.Maps;
import com.mysema.edith.Identifiable;
import com.mysema.util.BeanMap;
import com.mysema.util.ReflectionUtils;

@SuppressWarnings("unchecked")
public final class Converter {
    
    private static final Map<Class<?>, Class<?>> containerTypes = Maps.newHashMap();
    
    static {
        containerTypes.put(List.class, ArrayList.class);
        containerTypes.put(Set.class, HashSet.class);
        containerTypes.put(Collection.class, ArrayList.class);
    }
    
    public static <F, T> Collection<T> convert(Collection<F> coll1, Collection<T> coll2, Class<T> targetClass) {
        try {
            if (!coll1.isEmpty()) {
                if (Identifiable.class.isInstance(coll1.iterator().next()) && targetClass.equals(Long.class)) {
                    for (F bean : coll1) {
                        coll2.add((T)((Identifiable)bean).getId());
                    }
                } else {
                    for (F bean : coll1) {                
                        coll2.add((T)convert(new BeanMap(bean), new BeanMap(targetClass.newInstance())));                                
                    }    
                }                    
            }            
            return coll2;
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static <F, T> T convert(F source, T target) {
        try {
            if (source != null) {
                return (T)convert(new BeanMap(source), new BeanMap(target));    
            } else {
                return null;
            }     
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private static Object convert(BeanMap source, BeanMap target) throws InstantiationException, IllegalAccessException {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            if (entry.getKey().equals("class")) {
                continue;
            }
                  
            Class<?> type = target.getType(entry.getKey());
            if (type != null) {
                Class<?> sourceType = source.getType(entry.getKey());
                Object sourceValue = source.get(entry.getKey());
                Object targetValue = null;
                if (Collection.class.isAssignableFrom(type) && Collection.class.isAssignableFrom(sourceType)) {
                    Collection sourceColl = (Collection)sourceValue;
                    Collection targetColl = (Collection) containerTypes.get(type).newInstance();
                    targetValue = targetColl;
                    if (!sourceColl.isEmpty()) {
                        Type genericType = target.getReadMethod(entry.getKey()).getGenericReturnType();
                        Class elementType = ReflectionUtils.getTypeParameter(genericType, 0);
                        if (!elementType.isInstance(sourceColl.iterator().next())) {
                            convert(sourceColl, targetColl, elementType);    
                        }                                
                    }
                } else if (Map.class.isAssignableFrom(type)) {
                    // TODO
                } else if (sourceType.equals(type)) {
                    targetValue = sourceValue;
                } else { // Bean
                    targetValue = convert(sourceValue, type.newInstance());
                }
                target.put(entry.getKey(), targetValue);
            }
        }
        return target.getBean();

    }
    
    private Converter() {}

}
