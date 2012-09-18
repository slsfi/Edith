/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import javax.naming.event.EventContext;

import com.mysema.edith.domain.Place;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.PlaceDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
@Import(library = { "classpath:js/jquery-1.4.1.js", "deleteDialog.js" })
public class PlaceSearch {

    @Property
    private String searchTerm;

    private Context context;

    @Property
    private GridDataSource places;

    @Property
    private Place place;

    @Inject
    private NoteDao noteDao;

    @Inject
    private PlaceDao placeDao;

    void onActivate(EventContext ctx) {
        if (ctx.getCount() >= 1) {
            searchTerm = ctx.get(String.class, 0);
        }
        context = new Context(ctx);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    public void setupRender() {
        places = noteDao.queryPlaces(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    void onActionFromDelete(long placeId) {
        placeDao.remove(placeId);
    }

}
