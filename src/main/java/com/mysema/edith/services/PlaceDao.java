/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import java.util.List;

import com.mysema.edith.domain.Place;

/**
 * @author tiwe
 *
 */
public interface PlaceDao extends Dao<Place, Long> {

    /**
     * @param partial
     * @param limit
     * @return
     */
    List<Place> findByStartOfName(String partial, int limit);

    /**
     * @param placeId
     */
    void remove(Long placeId);

    /**
     * @param place
     */
    Place save(Place place);

}
