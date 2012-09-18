/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import javax.naming.event.EventContext;

import com.mysema.edith.domain.Person;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.PersonDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
@Import(library = { "classpath:js/jquery-1.4.1.js", "deleteDialog.js" })
public class PersonSearch {

    @Property
    private String searchTerm;

    private Context context;

    @Property
    private GridDataSource persons;

    @Property
    private Person person;

    @Inject
    private NoteDao noteDao;

    @Inject
    private PersonDao personDao;

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
        persons = noteDao.queryPersons(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    void onActionFromDelete(long personId) {
        personDao.remove(personId);
    }

    public String getTimeOfBirth() {
        if (person.getTimeOfBirth() != null) {
            return person.getTimeOfBirth().asString();
        }
        return null;
    }

    public String getTimeOfDeath() {
        if (person.getTimeOfDeath() != null) {
            return person.getTimeOfDeath().asString();
        }
        return null;
    }
}
