/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.EnumSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.*;

/**
 * AnnotatePage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js", "classpath:TapestryExt.js",
        "TextSelector.js", "AnnotatePage.js" })
@IncludeStylesheet("context:styles/tei.css")
public class AnnotatePage extends AbstractDocumentPage {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatePage.class);

    @Inject
    @Property
    private Block notesList;

    @Inject
    private Block noteEdit;

    @Inject
    private Block emptyBlock;

    @Inject
    private Block infoBlock;

    @Inject
    private Block errorBlock;

    @Inject
    private Messages messages;

    @Property
    private String infoMessage;

    @Inject
    @Property
    private Block documentView;

    @Property
    private List<NoteRevision> selectedNotes;

    @Property
    private NoteRevision noteOnEdit;

    @Property
    private Term termOnEdit;

    @Property
    private NoteRevision note;

    @Inject
    private RenderSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;

    @Inject
    private NoteRepository noteRepo;

    @Inject
    private TermRepository termRepo;

    @Property
    private List<NoteRevision> docNotes;

    @Property
    private SelectedText createTermSelection;

    @Property
    private SelectedText updateLongTextSelection;

    @Property
    private boolean moreThanOneSelectable;

    @Property
    private boolean submitSuccess;

    private static final String EDIT_ZONE = "editZone";

    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }

    void onActivate() {
        createTermSelection = new SelectedText();
        updateLongTextSelection = new SelectedText();
    }

    void setupRender() {
        docNotes = noteRevisionRepo.getOfDocument(getDocumentRevision());
    }

    Object onEdit(EventContext context) {
        selectedNotes = new ArrayList<NoteRevision>(context.getCount());
        for (int i = 0; i < context.getCount(); i++) {
            String localId = context.get(String.class, i);
            // XXX Where is this n coming?
            if (localId.startsWith("n")) {
                localId = localId.substring(1);
            }

            NoteRevision rev = noteRevisionRepo.getByLocalId(getDocumentRevision(), localId);
            if (rev != null) {
                selectedNotes.add(rev);
            } else {
                logger.error("Note with localId " + localId + " coundn't be found in "
                        + getDocumentRevision());
            }
        }

        if (selectedNotes.size() > 0) {
            noteOnEdit = selectedNotes.get(0);
            termOnEdit = getEditTerm(noteOnEdit);
        }

        // Order on lemma after we have selected the first one as a selection
        Collections.sort(selectedNotes, new NoteComparator());

        moreThanOneSelectable = selectedNotes.size() > 1;
        return noteEdit;
    }

    private static final class NoteComparator implements Comparator<NoteRevision>, Serializable {
        private static final long serialVersionUID = 1172304280333678242L;

        @Override
        public int compare(NoteRevision o1, NoteRevision o2) {
            return o1.getLemma().compareTo(o2.getLemma());
        }
    }

    private Term getEditTerm(NoteRevision noteRevision) {
        return noteRevision.getRevisionOf().getTerm() != null ? noteRevision.getRevisionOf()
                .getTerm().createCopy() : new Term();
    }

    Object onSuccessFromCreateTerm() throws IOException {
        logger.info(createTermSelection.toString());
        DocumentRevision documentRevision = getDocumentRevision();

        NoteRevision noteRevision = null;
        try {
            noteRevision = getDocumentRepo().addNote(documentRevision, createTermSelection);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }

        // prepare view (with new revision)
        documentRevision.setRevision(noteRevision.getSvnRevision());
        docNotes = noteRevisionRepo.getOfDocument(documentRevision);
        selectedNotes = Collections.singletonList(noteRevision);
        noteOnEdit = noteRevision;
        termOnEdit = getEditTerm(noteOnEdit);
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList).add(
                "documentZone", documentView);
    }

    void onPrepareForSubmit(String noteRev) {
        note = noteRevisionRepo.getById(noteRev).createCopy();
        noteOnEdit = note;
        termOnEdit = getEditTerm(noteOnEdit);
    }

    private void updateSetContents(Set<NameForm> nameForms, String name, String description) {
        if (name != null) {
            nameForms.add(new NameForm(name, description));
        }
        // Removes name forms that don't have a name entered.
        Iterator<NameForm> iter = nameForms.iterator();
        while (iter.hasNext()) {
            NameForm current = iter.next();
            if (current.getName() == null) {
                iter.remove();
            }
        }
    }

    Object onSuccessFromNoteEditForm() throws IOException {
        NoteRevision noteRevision;
        if (note.getRevisionOf().getStatus().equals(NoteStatus.INITIAL)) {
            note.getRevisionOf().setStatus(NoteStatus.DRAFT);
        }
        updateSetContents(note.getPerson().getOtherForms(), newPersonName, newPersonDescription);
        newPersonName = null;
        newPersonDescription = null;
        updateSetContents(note.getPlace().getOtherForms(), newPlaceName, newPlaceDescription);
        newPlaceName = null;
        newPlaceDescription = null;
        try {
            if (updateLongTextSelection.isValid()) {
                noteRevision = getDocumentRepo().updateNote(note, updateLongTextSelection);
            } else {
                noteRevision = noteRevisionRepo.save(note);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }

        // Handling the embedded term edit
        if (StringUtils.isNotBlank(termOnEdit.getBasicForm())) {
            // The idea is that language can be changed without a new term being created. It is a
            // bit hard to follow I admit. -vema
            List<Term> terms = termRepo.findByBasicForm(termOnEdit.getBasicForm());
            Term term = terms.isEmpty() ? termOnEdit : null;
            for (Term current : terms) {
                if (termOnEdit.getMeaning().equals(current.getMeaning())) {
                    term = current;
                    term.setLanguage(termOnEdit.getLanguage());
                    break;
                }
            }
            if (term == null) {
                term = termOnEdit.createCopy();
            }
            termRepo.save(term);
            noteRevision.getRevisionOf().setTerm(term);
            noteRepo.save(noteRevision.getRevisionOf());
        }

        // prepare view (with possibly new revision)
        if (noteRevision.getSvnRevision() > getDocumentRevision().getRevision()) {
            getDocumentRevision().setRevision(noteRevision.getSvnRevision());
        }
        docNotes = noteRevisionRepo.getOfDocument(getDocumentRevision());
        selectedNotes = Collections.singletonList(noteRevision);
        noteOnEdit = noteRevision;
        termOnEdit = getEditTerm(noteOnEdit);
        submitSuccess = true;

        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList).add(
                "documentZone", documentView);
    }

    Object onDelete(EventContext context) throws IOException {
        note = noteRevisionRepo.getById(context.get(String.class, 0));
        DocumentRevision documentRevision = getDocumentRevision();
        documentRevision = getDocumentRepo().removeNotes(documentRevision, note.getRevisionOf());

        // prepare view with new revision
        getDocumentRevision().setRevision(documentRevision.getRevision());
        docNotes = noteRevisionRepo.getOfDocument(documentRevision);
        selectedNotes = Collections.emptyList();
        return new MultiZoneUpdate(EDIT_ZONE, emptyBlock).add("listZone", notesList).add(
                "documentZone", documentView);
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepo.findByStartOfBasicForm(partial, 10);
    }

    List<NameForm> onProvideCompletionsFromNormalizedName(String partial) {
        return null;
    }

    public Object[] getEditContext() {
        List<String> ctx = new ArrayList<String>(selectedNotes.size());
        // Adding the current note to head
        ctx.add(note.getLocalId());
        for (NoteRevision r : selectedNotes) {
            if (!r.equals(note)) {
                ctx.add(r.getLocalId());
            }
        }
        return ctx.toArray();
    }

    @Validate("required")
    public NoteFormat getFormat() {
        return noteOnEdit.getFormat();
    }

    public void setFormat(NoteFormat format) {
        noteOnEdit.setFormat(format);
    }

    public Set<NoteType> getSelectedTypes() {
        return noteOnEdit.getTypes();
    }

    @Validate("required")
    public void setLanguage(TermLanguage language) {
        termOnEdit.setLanguage(language);
    }

    public TermLanguage getLanguage() {
        return termOnEdit.getLanguage();
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        noteOnEdit.getRevisionOf().setStatus(status);
    }

    public NoteStatus getStatus() {
        return noteOnEdit.getRevisionOf().getStatus();
    }

    public EnumSelectModel getStatusModel() {
        NoteStatus[] availableStatuses = noteOnEdit.getRevisionOf().getStatus().equals(
                NoteStatus.INITIAL) ? new NoteStatus[] { NoteStatus.INITIAL, NoteStatus.DRAFT,
                NoteStatus.FINISHED } : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
        return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }

    public String getTimeOfBirth() {
        return noteOnEdit.getPerson().getTimeOfBirth() == null ? null : noteOnEdit.getPerson()
                .getTimeOfBirth().asString();
    }

    public String getTimeOfDeath() {
        return noteOnEdit.getPerson().getTimeOfDeath() == null ? null : noteOnEdit.getPerson()
                .getTimeOfDeath().asString();
    }

    public void setTimeOfBirth(String time) {
        if (time != null) {
            noteOnEdit.getPerson().setTimeOfBirth(Interval.fromString(time));
        }
    }

    public void setTimeOfDeath(String time) {
        if (time != null) {
            noteOnEdit.getPerson().setTimeOfDeath(Interval.fromString(time));
        }
    }

    public NameForm getNormalizedPerson() {
        return noteOnEdit.getPerson().getNormalizedForm();
    }

    public NameForm getNormalizedPlace() {
        return noteOnEdit.getPlace().getNormalizedForm();
    }

    public Set<NameForm> getPersons() {
        return noteOnEdit.getPerson().getOtherForms();
    }

    public Set<NameForm> getPlaces() {
        return noteOnEdit.getPlace().getOtherForms();
    }

    @Property
    private NameForm loopPerson;

    @Property
    private NameForm loopPlace;

    @Property
    private String newPersonName;

    @Property
    private String newPersonDescription;

    @Property
    private String newPlaceName;

    @Property
    private String newPlaceDescription;

    public NoteType[] getTypes() {
        return NoteType.values();
    }

    @Property
    private NoteType type;

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            getSelectedTypes().add(type);
        } else {
            getSelectedTypes().remove(type);
        }
    }
}
