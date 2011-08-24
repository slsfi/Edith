/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.sql.domain.QTerm.term;

import java.util.List;

import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.ui.services.TermDao;

public class TermDaoImpl extends AbstractDao<Term> implements TermDao {

    @Override
    public List<Term> findByStartOfBasicForm(String partial, int maxResults) {
        return query().from(term).where(term.basicForm.startsWith(partial)).limit(maxResults)
                .list(term);
    }

    @Override
    public void remove(Long id) {
        Term term = getById(id);
        getSession().delete(term);
    }

    @Override
    public Term getById(Long id) {
        return query().from(term).where(term.id.eq(id)).uniqueResult(term);
    }

    @Override
    public void save(Term term) {
        getSession().save(term);
    }

}
