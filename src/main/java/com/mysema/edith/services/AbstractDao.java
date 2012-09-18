package com.mysema.edith.services;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mysema.edith.domain.QDocument;
import com.mysema.edith.domain.QDocumentNote;
import com.mysema.edith.domain.QNote;
import com.mysema.edith.domain.QNoteComment;
import com.mysema.edith.domain.QPerson;
import com.mysema.edith.domain.QPlace;
import com.mysema.edith.domain.QTerm;
import com.mysema.edith.domain.QUser;
import com.mysema.query.jpa.HQLTemplates;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;

public abstract class AbstractDao<T> implements Dao<T, Long> {

    protected static final QDocument document = QDocument.document;

    protected static final QDocumentNote documentNote = QDocumentNote.documentNote;

    protected static final QNote note = QNote.note;

    protected static final QNoteComment noteComment = QNoteComment.noteComment;

    protected static final QPerson person = QPerson.person;

    protected static final QPlace place = QPlace.place;

    protected static final QTerm term = QTerm.term;

    protected static final QUser user = QUser.user;

    @Inject
    private Provider<EntityManager> em;

    protected JPQLQuery query() {
        return new JPAQuery(em.get(), HQLTemplates.DEFAULT);
    }

    protected EntityManager getEntityManager() {
        return em.get();
    }

}