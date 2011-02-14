/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.Person;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.PersonRepository;

@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js", "deleteDialog.js" })
public class PersonSearchPage {

    @Property
    private String searchTerm;

    private Context context;

    @Property
    private GridDataSource persons;

    @Property
    private Person person;

    @Inject
    private NoteRepository noteRepository;

    @Inject
    private PersonRepository personRepository;

    @Environmental
    private RenderSupport support;

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
        persons = noteRepository.queryPersons(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    void onActionFromDelete(String personId) {
        personRepository.remove(personId);
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
