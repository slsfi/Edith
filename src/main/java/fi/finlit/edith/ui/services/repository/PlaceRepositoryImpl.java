/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.repository;

import static fi.finlit.edith.qtype.QPlace.place;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Place;
import fi.finlit.edith.ui.services.PlaceRepository;

public class PlaceRepositoryImpl extends AbstractRepository<Place> implements PlaceRepository {
    public PlaceRepositoryImpl(@Inject SessionFactory sessionFactory) {
        super(sessionFactory, place);
    }

    @Override
    public Collection<Place> findByStartOfName(String partial, int limit) {
        return getSession().from(place)
                .where(place.normalizedForm().last.startsWithIgnoreCase(partial)).limit(limit)
                .list(place);
    }

    @Override
    public void remove(String placeId) {
        Place entity = getById(placeId);
        if (entity != null){
            getSession().delete(entity);
        }
    }

    @Override
    public void save(Place place) {
        getSession().save(place);
    }

    @Override
    public void remove(Place place) {
        getSession().delete(place);
    }

}