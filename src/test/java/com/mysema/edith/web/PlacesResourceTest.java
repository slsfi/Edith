/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Place;
import com.mysema.edith.dto.PlaceTO;
import com.mysema.edith.services.PlaceDao;

public class PlacesResourceTest extends AbstractResourceTest {
    
    @Inject
    private PlaceDao placeDao;
    
    @Inject
    private PlacesResource places;
    
    @Test
    public void GetById() {
        Place place = new Place();
        place.setNormalized(new NameForm("a", "b"));
        place.setOtherForms(Sets.newHashSet(new NameForm("c","d")));
        placeDao.save(place);
        
        assertNotNull(places.getById(place.getId()));
    }

    @Test
    public void Add() {
        PlaceTO place = new PlaceTO();
        place.setNormalized(new NameForm("a", "b"));
        place.setOtherForms(Sets.newHashSet(new NameForm("c","d")));
        PlaceTO created = places.create(place);
        
        assertNotNull(created.getId());
    }
}
