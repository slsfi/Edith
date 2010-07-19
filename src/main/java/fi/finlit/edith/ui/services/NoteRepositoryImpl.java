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
import java.util.HashSet;

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

import fi.finlit.edith.domain.*;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryImpl extends AbstractRepository<Note> implements NoteRepository {

    private final TimeService timeService;

    private final UserRepository userRepository;

    private final AuthService authService;

    public NoteRepositoryImpl(@Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository, @Inject TimeService timeService,
            @Inject AuthService authService) {
        super(sessionFactory, note);
        this.userRepository = userRepository;
        this.timeService = timeService;
        this.authService = authService;
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
        rev.setPerson(new Person(new NameForm(), new HashSet<NameForm>()));
        rev.setPlace(new Place(new NameForm(), new HashSet<NameForm>()));
        getSession().save(rev);

        Note newNote = new Note();
        newNote.setDocument(docRevision.getDocument());
        newNote.setLocalId(localId);
        newNote.setLatestRevision(rev);
        rev.setRevisionOf(newNote);
        getSession().save(newNote);

        return newNote;
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

        LoopContext data = new LoopContext();

        Session session = getSession();

        while (true) {
            int event = -1;
            try {
                event = reader.next();
            } catch (XMLStreamException e) {
                throw new ServiceException(e);
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                handleStartElement(reader, data);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                handleEndElement(reader, session, data);
            } else if (event == XMLStreamConstants.CHARACTERS) {
                data.text = reader.getText();
            } else if (event == XMLStreamConstants.END_DOCUMENT) {
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

    private void handleStartElement(XMLStreamReader reader, LoopContext data) {
        String localName = reader.getLocalName();
        if (localName.equals("note")) {
            data.revision = new NoteRevision();
            data.revision.setRevisionOf(new Note());
            data.revision.getRevisionOf().setLatestRevision(data.revision);
            data.revision.setCreatedOn(timeService.currentTimeMillis());
        }
    }

    private void handleEndElement(XMLStreamReader reader, Session session, LoopContext data) {
        String localName = reader.getLocalName();

        if (localName.equals("note")) {
            session.save(data.revision.getRevisionOf());
            session.save(data.revision);
            data.counter++;
        } else if (localName.equals("lemma")) {
            data.revision.setLemma(data.text);
        } else if (localName.equals("lemma-meaning")) {
            data.revision.setLemmaMeaning(data.text);
        } else if (localName.equals("source")) {
            data.revision.setSources(data.text);
        } else if (localName.equals("description")) {
            data.revision.setDescription(data.text);
        }
    }

    private static final class LoopContext {
        private NoteRevision revision;
        private String text;
        private int counter;

        private LoopContext() {
            revision = null;
            text = null;
            counter = 0;
        }
    }

    @Override
    public GridDataSource queryDictionary(String searchTerm) {
        Assert.notNull(searchTerm);
        if (!searchTerm.equals("*")) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(termWithNotes.basicForm.contains(searchTerm, false));
            builder.or(termWithNotes.meaning.contains(searchTerm, false));
            return createGridDataSource(termWithNotes, termWithNotes.basicForm.lower().asc(),
                    false, builder.getValue());
        }
        return createGridDataSource(termWithNotes, termWithNotes.basicForm.lower().asc(), false);
    }

    @Override
    public void remove(Note noteToBeRemoved, long revision) {
        Assert.notNull(noteToBeRemoved, "note was null");

        UserInfo createdBy = userRepository.getCurrentUser();
        NoteRevision noteRevision = noteToBeRemoved.getLatestRevision().createCopy();
        noteRevision.setCreatedOn(timeService.currentTimeMillis());
        noteRevision.setCreatedBy(createdBy);
        noteRevision.setSVNRevision(revision);
        noteRevision.setDeleted(true);
        noteToBeRemoved.setLatestRevision(noteRevision);

        getSession().save(noteRevision);
        getSession().save(noteToBeRemoved);
    }

    @Override
    public NoteComment createComment(Note note, String message) {
        NoteComment comment = new NoteComment(note, message, authService.getUsername());
        getSession().save(comment);
        return comment;
    }

    @Override
    public NoteComment removeComment(String commentId) {
        NoteComment comment = getSession().getById(commentId, NoteComment.class);
        getSession().delete(comment);
        return comment;
    }
}
