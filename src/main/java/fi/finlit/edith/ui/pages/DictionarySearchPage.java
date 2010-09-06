/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.TermWithNotes;

/**
 * DictionarySearchPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class DictionarySearchPage {

    @Property
    private String searchTerm;

    private Context context;

    @Property
    private GridDataSource terms;

    @Property
    private TermWithNotes term;

    @Property
    private Note note;

    @Inject
    private NoteRepository noteRepository;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Environmental
    private RenderSupport support;

    void onActivate(EventContext ctx) {
        if (ctx.getCount() >= 1) {
            searchTerm = ctx.get(String.class, 0);
        }
        context = new Context(ctx);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    public void setupRender() {
        terms = noteRepository.queryDictionary(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    public String getLongTexts() {
        Collection<String> longTexts = new ArrayList<String>();
        for (DocumentNote documentNote : documentNoteRepository.getOfNote(note.getId())) {
            longTexts.add(documentNote.getLongText());
        }
        return StringUtils.join(longTexts, ", ");
    }
}
