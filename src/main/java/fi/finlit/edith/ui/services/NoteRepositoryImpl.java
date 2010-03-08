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
import java.io.FileNotFoundException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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
    public int importNotes(File file) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(new FileInputStream(file));
        } catch (XMLStreamException e) {
            throw new ServiceException(e);
        } catch (FileNotFoundException e) {
            throw new ServiceException(e);
        }

        LoopData data = new LoopData();

        Session session = getSession();

        while (true) {
            int event = -1;
            try {
                event = reader.next();
            } catch (XMLStreamException e) {
                throw new ServiceException(e);
            }
            if (event == XMLStreamConstants.START_ELEMENT){
                handleStartElement(reader, data);
            }else if (event == XMLStreamConstants.END_ELEMENT){
                handleEndElement(reader, session, data);
            }else if (event == XMLStreamConstants.CHARACTERS){
                data.text = reader.getText();
            }else if (event == XMLStreamConstants.END_DOCUMENT) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    throw new ServiceException(e);
                }
                break;
            }
        }
        return data.counter;
    }

    private void handleStartElement(XMLStreamReader reader, LoopData data) {
        String localName = reader.getLocalName();
        if (localName.equals("note")){
            data.revision = new NoteRevision();
            data.revision.setRevisionOf(new Note());
            data.revision.getRevisionOf().setLatestRevision(data.revision);
            data.revision.setCreatedOn(timeService.currentTimeMillis());
            data.term = null;
        }
    }

    private void handleEndElement(XMLStreamReader reader, Session session, LoopData data) {
        String localName = reader.getLocalName();

        if (localName.equals("note")){
            if (data.term != null) {
                data.revision.getRevisionOf().setTerm(data.term);
                session.save(data.term);
            }
            session.save(data.revision.getRevisionOf());
            session.save(data.revision);
            data.counter++;
        }else if (localName.equals("lemma")){
            data.revision.setLemma(data.text);
        }else if (localName.equals("text")){
            data.revision.setLongText(data.text);
        }else if (localName.equals("baseform")){
            if (data.term == null) {
                data.term = new Term();
            }
            data.term.setBasicForm(data.text);
        }else if (localName.equals("meaning")){
            if (data.term == null) {
                data.term = new Term();
            }
            data.term.setMeaning(data.text);
        }else if (localName.equals("description")){
            data.revision.setDescription(data.text);
        }

    }

    private static class LoopData {
        private NoteRevision revision;
        private Term term;
        private String text;
        private int counter;

        private LoopData() {
            revision = null;
            term = null;
            text = null;
            counter = 0;
        }
    }

    @Override
    public GridDataSource queryDictionary(String searchTerm) {
        Assert.notNull(searchTerm);
        if (!searchTerm.equals("*")){
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(termWithNotes.basicForm.contains(searchTerm, false));
            builder.or(termWithNotes.meaning.contains(searchTerm, false));
            return createGridDataSource(termWithNotes, termWithNotes.basicForm.asc(), builder.getValue());
        }
        return createGridDataSource(termWithNotes, termWithNotes.basicForm.asc());
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

        getSession().save(noteRevision);
        getSession().save(note);
    }

}
