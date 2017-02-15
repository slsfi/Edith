package com.mysema.edith.web;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.DocumentTO;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.UserDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DocumentsResourceTest extends AbstractResourceTest {
    
    @Inject @Named(EdithTestConstants.TEST_DOCUMENT_KEY)
    private String testDocument;
    
    @Inject
    private DocumentDao documentDao;
    
    @Inject
    private DocumentsResource documents;
    
    @Inject
    private NoteDao noteDao;
    
    @Inject
    private UserDao userDao;
    
    private Note note;
    
    private Note createNote(User editor) {
        Note note = new Note();
        note.setTerm(new Term());
        note.setAllEditors(Sets.newHashSet(editor));
        note.setLastEditedBy(editor);
        return note;
    }
    
    @Before
    public void setUp() {
        User editor = new User();
        editor.setUsername("test"+System.currentTimeMillis());
        userDao.save(editor);
        
        note  = createNote(editor);
        noteDao.save(note);
    }
    
    @Test
    public void GetById() {       
        Document document = documentDao.getDocumentForPath(testDocument);
        assertNotNull(documents.getById(document.getId()));
    }
    
    @Test
    public void Add() {
        DocumentTO doc = new DocumentTO();
        doc.setPath("abc" + System.currentTimeMillis());
        doc.setTitle("title");
        DocumentTO created = documents.create(doc);
        
        assertNotNull(created.getClass());
    }
    
/*    @Test
    public void Create_Selection() throws IOException, NoteAdditionFailedException {
        String PREFIX = "TEI-text0-body0-";
        Document document = documentDao.getDocumentForPath(testDocument);
        String element = "div0-div1-sp1-p0";
        String text = "sun ullakosta ottaa";
        
        SelectionTO selection = new SelectionTO();
        selection.setNoteId(note.getId());
        selection.setText(new SelectedText(PREFIX + element, PREFIX + element, 1, 4, text));

        DocumentNoteTO created = documents.create(document.getId(), selection);
        
        assertNotNull(created.getId());
    }
*/
    @Test
    public void Update_Selection() {
        // TODO
    }


}
