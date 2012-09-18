package com.mysema.edith.services;

import java.util.Collection;

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
    Collection<Place> findByStartOfName(String partial, int limit);

    /**
     * @param placeId
     */
    void remove(Long placeId);

    /**
     * @param place
     */
    void save(Place place);

}
