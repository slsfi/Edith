package com.mysema.edith.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;

@Transactional
public class NoteCommentDaoTest extends AbstractHibernateTest {

    @Inject
    private NoteCommentDao dao;
    
    @Inject
    private NoteDao noteDao;
    
    private NoteComment comment;
    
    @Before
    public void setUp() {
        Note note = new Note();
        note.setLemma("foobar");
        noteDao.save(note);
        
        comment = new NoteComment();
        comment.setNote(note);
        comment.setMessage("msg");
        comment = dao.save(comment);
    }
    
    @Test
    public void Save() {        
        assertNotNull(dao.getById(comment.getId()));
    }
    
    @Test
    public void Remove() {
        dao.remove(comment.getId());        
        assertNull(dao.getById(comment.getId()));
    }
    
    @Test
    public void getOfNote() {
        assertNotNull(dao.getOneOfNote(comment.getNote().getId()));
    }
    
    
    
}
