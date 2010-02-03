/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

// TODO: Auto-generated Javadoc
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
    Term findByBasicForm(String basicForm);
}
