/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.NoteStatus;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermRepository;

/**
 * AnnotatePage provides
 * 
 * @author tiwe
 * @version $Id$
 */
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
        System.out.println("onProvideCompletionsFromTerm " + partial);

        List<Term> terms = termRepo.findByStartOfBasicForm(partial, 10);
        List<String> results = new ArrayList<String>(terms.size());
        for (Term term : terms) {
            results.add(term.getBasicForm());
        }
        return results;
    }

}
