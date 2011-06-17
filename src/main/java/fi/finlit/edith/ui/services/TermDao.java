/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.util.List;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import fi.finlit.edith.sql.domain.Term;

public interface TermDao extends Dao<Term, Long> {

    /**
     * Find matching terms by searching matches from basicForm -property.
     * 
     * @param partial
     *            the start of the basicForm
     * @param maxResults
     *            the max results
     * 
     */
    List<Term> findByStartOfBasicForm(String partial, int maxResults);

    /**
     * @param basicForm
     * @return
     */
    List<Term> findByBasicForm(String basicForm);

    @CommitAfter
    void remove(Long termId);

    /**
     * @param term
     */
    @CommitAfter
    void save(Term term);
}
