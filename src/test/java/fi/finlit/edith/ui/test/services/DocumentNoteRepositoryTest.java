/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

/**
 * NoteRevisionRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentNoteRepositoryTest extends AbstractServiceTest {
    @Inject
    @Symbol(ServiceTestModule.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject
    private NoteRepository noteRepo;

    @Inject
    private AdminService adminService;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private DocumentRepository documentRepository;

    private Document document;

    private DocumentRevision docRev;

    private long latestRevision;

    @Test
    public void Save_Document_Note_With_An_Existing_Lemma_Is_Mapped_To_The_Existing_Note() {
        final String lemmaMeaning = "a legendary placeholder";
        final String lemma = "foobar";

        DocumentNote dn1 = new DocumentNote();
        dn1.setLocalId("1");
        Note n1 = new Note();
        n1.setLemma(lemma);
        n1.setLemmaMeaning(lemmaMeaning);
        dn1.setNote(n1);

        DocumentNote dn2 = new DocumentNote();
        dn2.setLocalId("2");
        Note n2 = new Note();
        n2.setLemma(lemma);
        dn2.setNote(n2);

        documentNoteRepository.save(dn1);
        documentNoteRepository.save(dn2);

        DocumentNote persisted1 = documentNoteRepository.getById(dn1.getId());
        DocumentNote persisted2 = documentNoteRepository.getById(dn2.getId());

        assertEquals(lemma, persisted1.getNote().getLemma());
        assertEquals(lemma, persisted2.getNote().getLemma());
        assertEquals(lemmaMeaning, persisted1.getNote().getLemmaMeaning());
        assertEquals(lemmaMeaning, persisted2.getNote().getLemmaMeaning());
        assertEquals("1", persisted1.getLocalId());
        assertEquals("2", persisted2.getLocalId());
    }

    @Test
    public void getByLocalId() {
        assertNotNull(documentNoteRepository.getByLocalId(docRev, "1"));
        assertNotNull(documentNoteRepository.getByLocalId(docRev, "2"));
        assertNotNull(documentNoteRepository.getByLocalId(docRev, "3"));
        assertNotNull(documentNoteRepository.getByLocalId(docRev, "4"));
    }

    @Test
    public void getOfDocument() {
        assertEquals(4, documentNoteRepository.getOfDocument(docRev).size());
    }

    @Test
    public void getOfDocument_with_note_updates() {
        assertEquals(4, documentNoteRepository.getOfDocument(docRev).size());

        for (DocumentNote documentNote : documentNoteRepository.getOfDocument(docRev)) {
            documentNote = documentNote.createCopy();
            documentNote.getNote().setLemma(documentNote.getNote().getLemma() + "XXX");
            documentNoteRepository.save(documentNote);
        }

        assertEquals(4, documentNoteRepository.getOfDocument(docRev).size());
    }

    @Override
    protected Class<?> getServiceClass() {
        return DocumentNoteRepository.class;
    }

    @Test
    public void queryNotes() {
        assertTrue(documentNoteRepository.queryNotes("annoit").getAvailableRows() > 0);
    }

    @Test
    public void queryNotes_sorting_is_case_insensitive() {
        noteRepo.createNote(docRev, "5", "a");
        noteRepo.createNote(docRev, "6", "b");
        noteRepo.createNote(docRev, "7", "A");
        noteRepo.createNote(docRev, "8", "B");
        GridDataSource gds = documentNoteRepository.queryNotes("*");
        int n = gds.getAvailableRows();
        List<SortConstraint> sortConstraints = new ArrayList<SortConstraint>();
        sortConstraints.add(new SortConstraint(new PropertyModelMock(), ColumnSort.ASCENDING));
        gds.prepare(0, 100, sortConstraints);
        String previous = null;
        for (int i = 0; i < n; ++i) {
            String current = gds.getRowValue(i).toString().toLowerCase();
            if (previous != null) {
                assertThat("The actual value was probably in upper case!", previous,
                        lessThanOrEqualTo(current));
            }
            previous = current;
        }
    }

    @Test
    @Ignore
    public void remove() {
        fail("Not yet implemented");
    }

    @Before
    public void setUp() {
        adminService.removeNotesAndTerms();

        document = documentRepository.getDocumentForPath(testDocument);
        List<RevisionInfo> revisions = documentRepository.getRevisions(document);
        latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        docRev = document.getRevision(latestRevision);
        noteRepo.createNote(docRev, "1", "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen");
        noteRepo.createNote(docRev, "2",
        "koska suutarille k\u00E4skyn k\u00E4r\u00E4jiin annoit, saadaksesi naimalupaa.");
        noteRepo.createNote(docRev, "3", "tulee, niin seisoo s\u00E4\u00E4t\u00F6s-kirjassa.");
        noteRepo
        .createNote(docRev, "4",
        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
    }

    @Test
    public void Store_And_Retrieve_Person_Note() {
        DocumentNote documentNote = noteRepo
        .createNote(docRev, "3",
        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PERSON);
        NameForm normalizedForm = new NameForm("Aleksis",  "Kivi", "Suomen hienoin kirjailija ikinä.");
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm("Alexis", "Stenvall", "En jättebra skrivare."));
        note.setPerson(new Person(normalizedForm, otherForms));
        Interval timeOfBirth = Interval.createDate(new LocalDate(1834, 10, 10));
        Interval timeOfDeath = Interval.createDate(new LocalDate(1872, 12, 31));
        note.getPerson().setTimeOfBirth(timeOfBirth);
        note.getPerson().setTimeOfDeath(timeOfDeath);
        noteRepo.save(note);
        Note persistedNote = documentNoteRepository.getById(documentNote.getId()).getNote();
        assertEquals(note.getPerson().getNormalizedForm().getName(), persistedNote
                .getPerson().getNormalizedForm().getName());
        assertEquals(note.getPerson().getNormalizedForm().getDescription(),
                persistedNote.getPerson().getNormalizedForm().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());
        assertEquals(note.getPerson().getTimeOfBirth().getDate(), persistedNote
                .getPerson().getTimeOfBirth().getDate());
        assertEquals(note.getPerson().getTimeOfDeath().getDate(), persistedNote
                .getPerson().getTimeOfDeath().getDate());
    }

    @Test
    public void Store_And_Retrieve_Person_With_The_Same_Birth_And_Death_Date() {
        DocumentNote documentNote = noteRepo
        .createNote(docRev, "3",
        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PERSON);
        Interval timeOfBirth = Interval.createYear(1834);
        Interval timeOfDeath = Interval.createYear(1834);
        note.setPerson(new Person());
        note.getPerson().setTimeOfBirth(timeOfBirth);
        note.getPerson().setTimeOfDeath(timeOfDeath);
        noteRepo.save(note);
        Note persistedNote = documentNoteRepository.getById(documentNote.getId()).getNote();
        assertNotNull(persistedNote.getPerson().getTimeOfBirth());
        assertNotNull(persistedNote.getPerson().getTimeOfDeath());
    }

    @Test
    public void Store_And_Retrieve_Place_Note() {
        DocumentNote documentNote = noteRepo
        .createNote(docRev, "3",
        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PLACE);
        NameForm normalizedForm = new NameForm("Tampere", "Kaupunki Hämeessä.");
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm("Tammerfors", "Ruotsinkielinen nimitys."));
        note.setPlace(new Place(normalizedForm, otherForms));
        noteRepo.save(note);
        Note persistedNote = documentNoteRepository.getById(documentNote.getId()).getNote();
        assertEquals(note.getPlace().getNormalizedForm().getName(), persistedNote
                .getPlace().getNormalizedForm().getName());
        assertEquals(note.getPlace().getNormalizedForm().getDescription(),
                persistedNote.getPlace().getNormalizedForm().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());
    }

    private static final class PropertyModelMock implements PropertyModel {

        @Override
        public PropertyModel dataType(String dataType) {
            return null;
        }

        @Override
        public PropertyConduit getConduit() {
            return null;
        }

        @Override
        public String getDataType() {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public String getLabel() {
            return null;
        }

        @Override
        public String getPropertyName() {
            return "longText";
        }

        @Override
        public Class<String> getPropertyType() {
            return String.class;
        }

        @Override
        public boolean isSortable() {
            return true;
        }

        @Override
        public PropertyModel label(String label) {
            return null;
        }

        @Override
        public BeanModel<DocumentNote> model() {
            return null;
        }

        @Override
        public PropertyModel sortable(boolean sortable) {
            return null;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return null;
        }

    }
}
