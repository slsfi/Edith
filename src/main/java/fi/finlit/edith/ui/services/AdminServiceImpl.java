/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;
import fi.finlit.edith.domain.Term;

public class AdminServiceImpl extends AbstractService implements AdminService{

    public AdminServiceImpl(@Inject SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void removeNotes() {
        Session session = getSession();
        removeAll(session, DocumentNote.class);
        removeAll(session, Note.class);
        session.flush();
    }

    @Override
    public void removeNotesAndTerms() {
        Session session = getSession();
        removeAll(session, DocumentNote.class);
        removeAll(session, Note.class);
        removeAll(session, Term.class);
        removeAll(session, Person.class);
        removeAll(session, Place.class);
        session.flush();
    }

    private <T> void removeAll(Session session, Class<T> type){
        for (T instance : session.findInstances(type)){
            session.delete(instance);
        }
    }

}
