/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fi.finlit.edith.domain.Term;

/**
 * Term repository.
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface TermRepository extends Repository<Term,String>{

    /**
     * Find matching terms by searching matches from basicForm -property.
     *
     * @param partial the start of the basicForm
     * @param maxResults the max results
     *
     */
    List<Term> findByStartOfBasicForm(String partial, int maxResults);

    /**
     * @param basicForm
     * @return
     */
    List<Term> findByBasicForm(String basicForm);
}
