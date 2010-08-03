/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
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

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Interval;
import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;
import fi.finlit.edith.domain.UserInfo;
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

    @Inject
    private SessionFactory sessionFactory;

    private Document document;

    private DocumentRevision docRev;

    private long latestRevision;

    private DocumentNoteSearchInfo searchInfo;

    @Test
    public void Save_Document_Note_With_An_Existing_Lemma_Is_Mapped_To_The_Existing_Note() {
        final String lemmaMeaning = "a legendary placeholder";
        final String lemma = "foobar";
        Document doc = new Document();

        DocumentNote dn1 = new DocumentNote();
        dn1.setDocument(doc);
        dn1.setLocalId("1");
        Note n1 = new Note();
        n1.setLemma(lemma);
        n1.setLemmaMeaning(lemmaMeaning);
        dn1.setNote(n1);

        DocumentNote dn2 = new DocumentNote();
        dn2.setDocument(doc);
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

        assertEquals(2, documentNoteRepository.getOfDocument(persisted2.getDocumentRevision())
                .size());
    }

    @Test
    public void Change_Backing_Note_To_Another_Note() {
        final String lemmaMeaning = "a legendary placeholder";
        final String lemma = "foobar";
        Document doc = new Document();

        DocumentNote dn1 = new DocumentNote();
        dn1.setDocument(doc);
        dn1.setLocalId("1");
        Note n1 = new Note();
        n1.setLemma(lemma);
        n1.setLemmaMeaning(lemmaMeaning);
        dn1.setNote(n1);

        DocumentNote dn2 = new DocumentNote();
        dn2.setDocument(doc);
        dn2.setLocalId("2");
        Note n2 = new Note();
        n2.setLemma("barfoo");
        dn2.setNote(n2);

        documentNoteRepository.save(dn1);
        documentNoteRepository.save(dn2);

        DocumentNote persisted1 = documentNoteRepository.getById(dn1.getId());
        DocumentNote persisted2 = documentNoteRepository.getById(dn2.getId());

        persisted2.getNote().setLemma(lemma);

        documentNoteRepository.save(persisted2);

        persisted2 = documentNoteRepository.getById(persisted2.getId());

        assertEquals(lemma, persisted1.getNote().getLemma());
        assertEquals(lemma, persisted2.getNote().getLemma());
        assertEquals(lemmaMeaning, persisted1.getNote().getLemmaMeaning());
        assertEquals(lemmaMeaning, persisted2.getNote().getLemmaMeaning());
        assertEquals("1", persisted1.getLocalId());
        assertEquals("2", persisted2.getLocalId());

        assertEquals(2, documentNoteRepository.getOfDocument(persisted2.getDocumentRevision())
                .size());
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
        noteRepo.createNote(docRev, "4",
                "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        searchInfo = new DocumentNoteSearchInfo();
        addExtraNote("testo");
        addExtraNote("testo2");
    }

    @Test
    public void Store_And_Retrieve_Person_Note() {
        DocumentNote documentNote = noteRepo
                .createNote(docRev, "3",
                        "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
        Note note = documentNote.getNote();
        note.setFormat(NoteFormat.PERSON);
        NameForm normalizedForm = new NameForm("Aleksis", "Kivi",
                "Suomen hienoin kirjailija ikinä.");
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm("Alexis", "Stenvall", "En jättebra skrivare."));
        note.setPerson(new Person(normalizedForm, otherForms));
        Interval timeOfBirth = Interval.createDate(new LocalDate(1834, 10, 10));
        Interval timeOfDeath = Interval.createDate(new LocalDate(1872, 12, 31));
        note.getPerson().setTimeOfBirth(timeOfBirth);
        note.getPerson().setTimeOfDeath(timeOfDeath);
        noteRepo.save(note);
        Note persistedNote = documentNoteRepository.getById(documentNote.getId()).getNote();
        assertEquals(note.getPerson().getNormalizedForm().getName(), persistedNote.getPerson()
                .getNormalizedForm().getName());
        assertEquals(note.getPerson().getNormalizedForm().getDescription(), persistedNote
                .getPerson().getNormalizedForm().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());
        assertEquals(note.getPerson().getTimeOfBirth().getDate(), persistedNote.getPerson()
                .getTimeOfBirth().getDate());
        assertEquals(note.getPerson().getTimeOfDeath().getDate(), persistedNote.getPerson()
                .getTimeOfDeath().getDate());
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
        assertEquals(note.getPlace().getNormalizedForm().getName(), persistedNote.getPlace()
                .getNormalizedForm().getName());
        assertEquals(note.getPlace().getNormalizedForm().getDescription(), persistedNote.getPlace()
                .getNormalizedForm().getDescription());
        assertEquals(note.getFormat(), persistedNote.getFormat());
    }

    @Test
    public void Query_For_All_Notes() {
        assertEquals(6, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Document() {
        searchInfo.getDocuments().add(docRev.getDocument());
        assertEquals(4, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Creator() {
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setUsername("testo");
        searchInfo.getCreators().add(userInfo1);
        assertEquals(1, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Creators() {
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setUsername("testo");
        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUsername("testo2");
        searchInfo.getCreators().add(userInfo1);
        searchInfo.getCreators().add(userInfo2);
        assertEquals(2, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Note_Type() {
        searchInfo.getNoteTypes().add(NoteType.HISTORICAL);
        assertEquals(2, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    @Ignore
    public void Query_For_Notes_Based_On_Note_Type_Two_Filters() {
        searchInfo.getNoteTypes().add(NoteType.HISTORICAL);
        searchInfo.getNoteTypes().add(NoteType.DICTUM);
        assertEquals(2, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    public void Query_For_Notes_Based_On_Note_Format() {
        searchInfo.getNoteFormats().add(NoteFormat.PERSON);
        assertEquals(2, documentNoteRepository.query(searchInfo).size());
    }

    @Test
    public void Get_Document_Notes_Of_Note() {
        List<DocumentNote> documentNotesOfDocument = documentNoteRepository.getOfDocument(docRev);
        assertFalse(documentNotesOfDocument.isEmpty());
        List<DocumentNote> documentNotesOfNote = documentNoteRepository.getOfNote(documentNotesOfDocument.get(0).getNote().getId());
        assertFalse(documentNotesOfNote.isEmpty());
    }

    private void addExtraNote(String username) {
        DocumentNote documentNote = new DocumentNote();
        documentNote.setDocument(new Document());
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        documentNote.setCreatedBy(userInfo);
        Note note = new Note();
        note.setLemma("thelemma");
        note.setTypes(new HashSet<NoteType>());
        note.getTypes().add(NoteType.HISTORICAL);
        note.setFormat(NoteFormat.PERSON);
        documentNote.setNote(note);
        documentNote.setLongText("thelongtext");
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.save(documentNote);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
