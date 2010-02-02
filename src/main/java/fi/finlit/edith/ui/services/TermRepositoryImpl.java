/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QTerm.term;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermRepository;

/**
 * NoteRepositoryImpl provides
 * 
 * @author tiwe
 * @version $Id$
 */
public class TermRepositoryImpl extends AbstractRepository<Term> implements TermRepository {
    
    public TermRepositoryImpl(@Inject SessionFactory sessionFactory, @Inject AuthService authService) {
        super(sessionFactory, term);
    }

    public List<Term> findByStartOfBasicForm(String partial, int maxResults) {
        return getSession().from(term).where(term.basicForm.startsWith(partial)).limit(maxResults)
                .list(term);
    }

    public Term findByBasicForm(String basicForm) {
        return getSession().from(term).where(term.basicForm.eq(basicForm)).uniqueResult(term);
    }    
    

}
