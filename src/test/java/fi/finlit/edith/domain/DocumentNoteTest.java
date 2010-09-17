package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DocumentNoteTest {
    private DocumentNote documentNote;

    @Before
    public void setUp() throws Exception {
        documentNote = new DocumentNote();
        documentNote.setCreatedBy(new UserInfo());
        documentNote.setCreatedOn(100000);
        documentNote.setDeleted(true);
        Document document = new Document();
        document.setSvnPath("documents/edith");
        documentNote.setDocRevision(new DocumentRevision(document, 10));
        documentNote.setDocument(document);
        documentNote.setLocalId("foobar");
        documentNote.setLongText("foobar is bar");
        documentNote.setNote(new Note());
        documentNote.setSVNRevision(10);
    }

    @Test
    public void createCopy() {
        DocumentNote copy = documentNote.createCopy();
        assertEquals(documentNote.getCreatedBy(), copy.getCreatedBy());
        assertEquals(documentNote.getCreatedOn(), copy.getCreatedOn());
        assertEquals(documentNote.getDocRevision(), copy.getDocRevision());
        assertEquals(documentNote.isDeleted(), copy.isDeleted());
        assertEquals(documentNote.getDocument(), copy.getDocument());
        assertEquals(documentNote.getLocalId(), copy.getLocalId());
        assertEquals(documentNote.getLongText(), copy.getLongText());
        assertEquals(documentNote.getNote(), copy.getNote());
        assertEquals(documentNote.getSVNRevision(), copy.getSVNRevision());
    }

}
