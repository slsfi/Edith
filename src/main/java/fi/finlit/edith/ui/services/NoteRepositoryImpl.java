/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QNote.note;
import static fi.finlit.edith.domain.QNoteRevision.noteRevision;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.paging.CallbackService;
import com.mysema.query.paging.ListSource;
import com.mysema.rdfbean.tapestry.PagedQuery;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryImpl extends AbstractRepository<Note> implements NoteRepository{

    @Inject
    private CallbackService txCallback;
    
    public NoteRepositoryImpl() {
        super(note);
    }

    private PagedQuery getPagedQuery(){
        return new PagedQuery(txCallback, getSession());
    }
    
    @Override
    public ListSource<NoteRevision> queryNotes(String searchTerm) {
        Assert.notNull(searchTerm);
        return getPagedQuery()
            .from(noteRevision)
            .where(noteRevision.lemma.contains(searchTerm, false))
            .orderBy(noteRevision.createdOn.desc())
            .list(noteRevision); 
    }

    @Override
    public int importNotes(File file) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(file));
        
        NoteRevision revision = null;
        String text = null;        
        int counter = 0;
        
        while (true) {
            int event = reader.next();
            
            if (event == XMLStreamConstants.START_ELEMENT){
                String localName = reader.getLocalName();    
                if (localName.equals("note")){
                    revision = new NoteRevision();
                }
                
            }else if (event == XMLStreamConstants.END_ELEMENT){
                String localName = reader.getLocalName();
                
                if (localName.equals("note")){
                    Note note = new Note();                    
                    note.setLatestRevision(revision);
                    revision.setRevisionOf(note);
                    getSession().save(note);
                    getSession().save(revision);
                    counter++;
                }else if (localName.equals("lemma")){
                    revision.setLemma(text);
                }else if (localName.equals("text")){
                    revision.setLongText(text);
                }else if (localName.equals("baseform")){
                    revision.setBasicForm(text);
                }else if (localName.equals("meaning")){
                    revision.setMeaning(text);
                }else if (localName.equals("subsource")){
                    
                }else if (localName.equals("citation")){
                    
                }else if (localName.equals("description")){
                    revision.setExplanation(text);
                }else if (localName.equals("source")){
                    
                }else if (localName.equals("classification")){
                    
                }else if (localName.equals("edited")){
                    
                }else if (localName.equals("stage")){
                    
                }else if (localName.equals("comment")){
                    
                }
                    
            }else if (event == XMLStreamConstants.CHARACTERS){
                text = reader.getText();
                
            }else if (event == XMLStreamConstants.END_DOCUMENT) {
                reader.close();
                break;
            }
        }
        return counter;
    }
    
}
