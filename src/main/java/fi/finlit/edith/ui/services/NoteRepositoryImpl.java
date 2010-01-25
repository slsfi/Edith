/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QNote.note;
import static fi.finlit.edith.domain.QTermWithNotes.termWithNotes;
import static fi.finlit.edith.domain.QUserInfo.userInfo;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.rdfbean.dao.AbstractRepository;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteStatus;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.UserInfo;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryImpl extends AbstractRepository<Note> implements NoteRepository{

    @Inject
    private AuthService authService;
    
    public NoteRepositoryImpl() {
        super(note);
    }
    
    @Override
    public Note createNote(Document document, long revision, String localId, String lemma, String longText) {
        UserInfo createdBy = getSession().from(userInfo)
            .where(userInfo.username.eq(authService.getUsername()))
            .uniqueResult(userInfo);  
        
        NoteRevision rev = new NoteRevision();
        rev.setCreatedOn(System.currentTimeMillis());
        rev.setCreatedBy(createdBy);
        rev.setSVNRevision(revision);
        rev.setLemma(lemma);
        rev.setLongText(longText);
        getSession().save(rev);
        
        Note note = new Note();
        note.setStatus(NoteStatus.Draft);
        note.setDocument(document);
        note.setLocalId(localId);
        note.setLatestRevision(rev);
        rev.setRevisionOf(note);
        getSession().save(note);
        
        return note;
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
                    revision.setRevisionOf(new Note());
                    revision.getRevisionOf().setLatestRevision(revision);
                    revision.setCreatedOn(System.currentTimeMillis());
                }
                
            }else if (event == XMLStreamConstants.END_ELEMENT){
                String localName = reader.getLocalName();
                
                if (localName.equals("note")){                    
                    if (revision.getRevisionOf().getTerm() != null){
                        getSession().save(revision.getRevisionOf().getTerm());    
                    }                    
                    getSession().save(revision.getRevisionOf());
                    getSession().save(revision);
                    counter++;
                }else if (localName.equals("lemma")){
                    revision.setLemma(text);
                }else if (localName.equals("text")){
                    revision.setLongText(text);
                }else if (localName.equals("baseform")){
                    revision.setBasicForm(text);
                }else if (localName.equals("meaning")){
                    Term term = new Term();
                    term.setBasicForm(revision.getBasicForm());
                    term.setMeaning(text);
                    revision.getRevisionOf().setTerm(term);
                }else if (localName.equals("subsource")){
                    
                }else if (localName.equals("citation")){
                    
                }else if (localName.equals("description")){
                    revision.setDescription(text);
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

    @Override
    public GridDataSource queryDictionary(String searchTerm) {
        Assert.notNull(searchTerm);        
        if (!searchTerm.equals("*")){
            BooleanBuilder builder = new BooleanBuilder();        
            builder.or(termWithNotes.basicForm.contains(searchTerm, false));
            builder.or(termWithNotes.meaning.contains(searchTerm, false));
            return createGridDataSource(termWithNotes, termWithNotes.basicForm.asc(), builder.getValue());    
        }else{
            return createGridDataSource(termWithNotes, termWithNotes.basicForm.asc());
        }
        
    }
    
}
