/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.Term;
import fi.finlit.edith.ui.services.TermRepository;

@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js" })
public class AutocompleteTestPage {

    @Inject
    private TermRepository termRepo;

    @Property
    private Term term;

    private static final Logger logger = LoggerFactory.getLogger(AutocompleteTestPage.class);

    void onActivate() {
        term = new Term();
    }

    List<String> onProvideCompletionsFromTerm(String partial) {
        logger.debug("onProvideCompletionsFromTerm " + partial);

        List<Term> terms = termRepo.findByStartOfBasicForm(partial, 10);
        List<String> results = new ArrayList<String>(terms.size());
        for (Term t : terms) {
            results.add(t.getBasicForm());
        }
        return results;
    }

}
