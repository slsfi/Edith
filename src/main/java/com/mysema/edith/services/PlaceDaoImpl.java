/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import java.util.List;

import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Place;
import com.mysema.edith.domain.QPlace;

@Transactional
public class PlaceDaoImpl extends AbstractDao<Place> implements PlaceDao {

    private static final QPlace place = QPlace.place;

    @Override
    public List<Place> findByStartOfName(String partial, int limit) {
        return from(place)
               .where(place.normalized.last.startsWithIgnoreCase(partial))
               .limit(limit).list(place);
    }

    @Override
    public void remove(Long placeId) {
        Place place = getById(placeId);
        remove(place);
    }

    @Override
    public Place save(Place place) {
        return persistOrMerge(place);
    }

    @Override
    public Place getById(Long id) {
        return find(Place.class, id);
    }

}