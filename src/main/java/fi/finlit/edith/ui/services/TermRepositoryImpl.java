/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QTerm.term;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Term;
import fi.finlit.edith.ui.services.repository.AbstractRepository;

public class TermRepositoryImpl extends AbstractRepository<Term> implements TermDao {

    public TermRepositoryImpl(@Inject SessionFactory sessionFactory) {
        super(sessionFactory, term);
    }

    @Override
    public List<Term> findByStartOfBasicForm(String partial, int maxResults) {
        return getSession().from(term).where(term.basicForm.startsWith(partial)).limit(maxResults)
                .list(term);
    }

    @Override
    public List<Term> findByBasicForm(String basicForm) {
        return getSession().from(term).where(term.basicForm.eq(basicForm)).list(term);
    }

    @Override
    public void remove(String id) {
        Term entity = getById(id);
        if (entity != null) {
            getSession().delete(entity);
        }
    }

    @Override
    public void save(Term term) {
        getSession().save(term);
    }

}
