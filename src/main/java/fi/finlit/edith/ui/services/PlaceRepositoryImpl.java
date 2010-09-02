/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QPlace.place;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Place;
import fi.finlit.edith.domain.PlaceRepository;

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
}