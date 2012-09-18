package com.mysema.edith.services;

import java.util.Collection;

import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Place;

@Transactional
public class PlaceDaoImpl extends AbstractDao<Place> implements PlaceDao {
    @Override
    public Collection<Place> findByStartOfName(String partial, int limit) {
        return query().from(place).where(place.normalized.last.startsWithIgnoreCase(partial))
                .limit(limit).list(place);
    }

    @Override
    public void remove(Long placeId) {
        Place place = getById(placeId);
        getEntityManager().remove(place);
    }

    @Override
    public void save(Place place) {
        getEntityManager().persist(place);
    }

    @Override
    public Place getById(Long id) {
        return query().from(place).where(place.id.eq(id)).uniqueResult(place);
    }

}