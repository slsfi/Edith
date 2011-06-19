/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.sql.domain.Person;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.PersonDao;

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
