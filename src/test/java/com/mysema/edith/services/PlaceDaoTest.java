/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Place;


public class PlaceDaoTest extends AbstractHibernateTest {

    @Inject
    private PlaceDao placeDao;

    private Place place;

    @Before
    public void Before() {
        place = new Place(new NameForm(), new HashSet<NameForm>());
        place.getNormalized().setLast("Stockholm");
        placeDao.save(place);
    }

    @Test
    public void Remove() {
        assertNotNull(placeDao.getById(place.getId()));
        placeDao.remove(place.getId());
        assertNull(placeDao.getById(place.getId()));
    }

    @Test
    public void Find_By_Start_Of_Name() {
        assertTrue(placeDao.findByStartOfName("d", 10).isEmpty());
        assertEquals(1, placeDao.findByStartOfName("St", 10).size());
        assertEquals(1, placeDao.findByStartOfName("st", 10).size());
        assertEquals(1, placeDao.findByStartOfName("ST", 10).size());
    }
}
