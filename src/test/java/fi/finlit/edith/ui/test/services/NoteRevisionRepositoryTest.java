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
import java.util.List;

import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.AdminService;
import fi.finlit.edith.ui.services.svn.RevisionInfo;


/**
 * NoteRevisionRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRevisionRepositoryTest extends AbstractServiceTest{

    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_KEY)
    private String testDocument;

    @Inject
    private NoteRepository noteRepo;

    @Inject
    private AdminService adminService;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;

    @Inject
    private DocumentRepository documentRepo;

    private Document document;

    private DocumentRevision docRev;

    private long latestRevision;

    @Before
    public void setUp() {
        adminService.removeNotesAndTerms();

        document = documentRepo.getDocumentForPath(testDocument);
        List<RevisionInfo> revisions = documentRepo.getRevisions(document);
        latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        docRev = document.getRevision(latestRevision);
        noteRepo.createNote(docRev, "1", "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen");
        noteRepo.createNote(docRev, "2", "koska suutarille k\u00E4skyn k\u00E4r\u00E4jiin annoit, saadaksesi naimalupaa.");
        noteRepo.createNote(docRev, "3", "tulee, niin seisoo s\u00E4\u00E4t\u00F6s-kirjassa.");
        noteRepo.createNote(docRev, "4", "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4");
    }

    @Test
    public void getByLocalId() {
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "1"));
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "2"));
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "3"));
        assertNotNull(noteRevisionRepo.getByLocalId(docRev, "4"));
    }

    @Test
    public void queryNotes(){
        assertTrue(noteRevisionRepo.queryNotes("annoit").getAvailableRows() > 0);
    }

    @Test
    public void queryNotes_sorting_is_case_insensitive() {
        noteRepo.createNote(docRev, "5", "a");
        noteRepo.createNote(docRev, "6", "b");
        noteRepo.createNote(docRev, "7", "A");
        noteRepo.createNote(docRev, "8", "B");
        GridDataSource gds = noteRevisionRepo.queryNotes("*");
        int n = gds.getAvailableRows();
        List<SortConstraint> sortConstraints = new ArrayList<SortConstraint>();
        sortConstraints.add(new SortConstraint(new PropertyModelMock(), ColumnSort.ASCENDING));
        gds.prepare(0, 100, sortConstraints);
        String previous = null;
        for (int i = 0; i < n; ++i) {
            String current = gds.getRowValue(i).toString().toLowerCase();
            if (previous != null) {
                assertThat(previous, lessThanOrEqualTo(current));
            }
            previous = current;
        }
    }

    private static final class PropertyModelMock implements PropertyModel {

        @Override
        public PropertyModel dataType(String dataType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PropertyConduit getConduit() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDataType() {
            return null;
        }

        @Override
        public String getId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getLabel() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getPropertyName() {
            return "lemma";
        }

        @Override
        public Class getPropertyType() {
            return NoteRevision.class;
        }

        @Override
        public boolean isSortable() {
            return true;
        }

        @Override
        public PropertyModel label(String label) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public BeanModel model() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PropertyModel sortable(boolean sortable) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Test
    public void getOfDocument(){
        assertEquals(4, noteRevisionRepo.getOfDocument(docRev).size());
    }

    @Test
    public void getOfDocument_with_note_updates() {
        assertEquals(4, noteRevisionRepo.getOfDocument(docRev).size());

        for (NoteRevision rev : noteRevisionRepo.getOfDocument(docRev)){
            rev = rev.createCopy();
            rev.setLemma(rev.getLemma() +"XXX");
            noteRevisionRepo.save(rev);
        }

        assertEquals(4, noteRevisionRepo.getOfDocument(docRev).size());
    }

    @Test
    @Ignore
    public void remove() {
        fail("Not yet implemented");
    }

    @Override
    protected Class<?> getServiceClass() {
        return NoteRevisionRepository.class;
    }
}
