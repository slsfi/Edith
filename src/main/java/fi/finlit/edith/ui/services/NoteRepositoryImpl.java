/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QNote.note;
import static fi.finlit.edith.domain.QTermWithNotes.termWithNotes;

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
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.domain.UserRepository;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryImpl extends AbstractRepository<Note> implements NoteRepository{

    private final TimeService timeService;

    private final UserRepository userRepository;

    public NoteRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository,
            @Inject TimeService timeService) {
        super(sessionFactory, note);
        this.userRepository = userRepository;
        this.timeService = timeService;
    }

    @Override
    public Note createNote(DocumentRevision docRevision, String localId, String longText) {
        UserInfo createdBy = userRepository.getCurrentUser();

        NoteRevision rev = new NoteRevision();
        rev.setCreatedOn(timeService.currentTimeMillis());
        rev.setCreatedBy(createdBy);
        rev.setSVNRevision(docRevision.getRevision());
        rev.setLongText(longText);
        rev.setLemmaFromLongText();        
        getSession().save(rev);

        Note note = new Note();
        note.setDocument(docRevision.getDocument());
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
        Term term = null;
        String text = null;
        int counter = 0;

        Session session = getSession();

        while (true) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT){
                String localName = reader.getLocalName();
                if (localName.equals("note")){
                    revision = new NoteRevision();
                    revision.setRevisionOf(new Note());
                    revision.getRevisionOf().setLatestRevision(revision);
                    revision.setCreatedOn(timeService.currentTimeMillis());
                    term = null;
                }

            }else if (event == XMLStreamConstants.END_ELEMENT){
                String localName = reader.getLocalName();

                if (localName.equals("note")){
                    if (term != null) {
                        revision.getRevisionOf().setTerm(term);
                        session.save(term);
                    }
                    session.save(revision.getRevisionOf());
                    session.save(revision);
                    counter++;
                }else if (localName.equals("lemma")){
                    revision.setLemma(text);
                }else if (localName.equals("text")){
                    revision.setLongText(text);
                }else if (localName.equals("baseform")){
                    if (term == null) {
                        term = new Term();
                    }
                    term.setBasicForm(text);
                }else if (localName.equals("meaning")){
                    if (term == null) {
                        term = new Term();
                    }
                    term.setMeaning(text);
                }else if (localName.equals("description")){
                    revision.setDescription(text);
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

    @Override
    public void remove(Note note, long revision) {
        Assert.notNull(note, "note was null");

        UserInfo createdBy = userRepository.getCurrentUser();
        NoteRevision noteRevision = note.getLatestRevision().createCopy();
        noteRevision.setCreatedOn(timeService.currentTimeMillis());
        noteRevision.setCreatedBy(createdBy);
        noteRevision.setSVNRevision(revision);
        noteRevision.setDeleted(true);
        note.setLatestRevision(noteRevision);

        getSession().save(revision);
        getSession().save(note);
    }

}
