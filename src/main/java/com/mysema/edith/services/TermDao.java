/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.util.List;

import com.mysema.edith.domain.Term;

/**
 * @author tiwe
 *
 */
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
     * @param termId
     */
    void remove(Long termId);

    /**
     * @param term
     */
    Term save(Term term);

}
