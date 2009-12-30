/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.query.paging.ListSource;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;

/**
 * NoteSearch provides
 * 
 * @author tiwe
 * @version $Id$
 */
public class NoteSearchPage {

	@Property
	private String searchTerm;

	@Property
	private ListSource<NoteRevision> notes;

	@Property
	private NoteRevision note;

	@Inject
	private Block searchResultsBlock;

	@Inject
	private NoteRepository noteRepository;

	Object onSuccessFromSearchForm() {

		notes = noteRepository.queryNotes(searchTerm);

		return searchResultsBlock;
	}

	Object onPassivate() {
		return searchTerm;
	}

}
