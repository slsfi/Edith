/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;

/**
 * NoteSearch provides
 * 
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class NoteSearchPage {

	@Property
	private String searchTerm;
	
	@Property
	private String editMode;
	
	private Context context;

	@Property
    private GridDataSource notes;

	@Property
	private NoteRevision note;

	@Inject
	private NoteRevisionRepository noteRevisionRepository;
	
	@Property
    private NotePrimaryKeyEncoder encoder;

	@Inject
	@Path("NoteSearchPage.css")
	private Asset stylesheet;

	@Environmental
	private RenderSupport support;

	void onPrepare() {
        encoder = new NotePrimaryKeyEncoder();
    }
	
	void onActionFromToggleEdit() {
		context = new Context(searchTerm, "edit");
	}
	
	void onActionFromCancel() {
        context = new Context(searchTerm);
    }
	
	void onActionFromDelete(String noteRevisionId) {
	    noteRevisionRepository.remove(noteRevisionId);
	}
	
	void onActivate(EventContext context) {
		if (context.getCount() >= 1) {
		    this.searchTerm = context.get(String.class, 0);
		}
		if (context.getCount() >= 2) {
            this.editMode = context.get(String.class, 1);
        }
        this.context = new Context(context);
	}
	
	void onSuccessFromSearch() {
	    context = new Context(searchTerm);
	}
	
	void onSuccessFromEdit() {
	    //TODO Validations
	    noteRevisionRepository.saveAll(encoder.getAllValues());
	    context = new Context(searchTerm);
	}
	
	void setupRender() {
	    notes = noteRevisionRepository.queryNotes(searchTerm == null ? "*" : searchTerm);
	}

	Object onPassivate() {
		return context == null ? null : context.toArray();
	}

	private class NotePrimaryKeyEncoder implements ValueEncoder<NoteRevision> {
		private final Map<String, NoteRevision> keyToValue = new HashMap<String, NoteRevision>();

		public String toClient(NoteRevision value) {
		    //System.out.println("toClient called " + value);
			return value.getId();
		}

		public NoteRevision toValue(String id) {
		    //System.out.println("toValue called "+ id);
			NoteRevision note = noteRevisionRepository.getById(id);
			keyToValue.put(id, note);
			return note;
		}

		public final List<NoteRevision> getAllValues() {
			List<NoteRevision> result = CollectionFactory.newList();

			for (Map.Entry<String, NoteRevision> entry : keyToValue.entrySet()) {
				result.add(entry.getValue());
			}

			return result;
		}
	};
	
	@AfterRender
	void addStylesheet() {
		//This is needed to have the page specific style sheet after
		//other css includes
		support.addStylesheetLink(stylesheet, null);
	}
}
