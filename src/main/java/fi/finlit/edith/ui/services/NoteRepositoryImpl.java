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
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.LinkElement;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.Paragraph;
import fi.finlit.edith.domain.StringElement;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.domain.UserRepository;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryImpl extends AbstractRepository<Note> implements NoteRepository {

    private static final class LoopContext {
        private DocumentNote documentNote;
        private String text;
        private Paragraph paragraphs;
        private int counter;
        private boolean inBib;
        private String attr;

        private LoopContext() {
            documentNote = null;
            text = null;
            counter = 0;
        }
    }

    private final TimeService timeService;

    private final UserRepository userRepository;

    private final AuthService authService;

    // TODO Move methods using documentNoteRepository to documentNoteRepository?
    private final DocumentNoteRepository documentNoteRepository;

    public NoteRepositoryImpl(@Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository, @Inject TimeService timeService,
            @Inject AuthService authService, @Inject DocumentNoteRepository documentNoteRepository) {
        super(sessionFactory, note);
        this.userRepository = userRepository;
        this.timeService = timeService;
        this.authService = authService;
        this.documentNoteRepository = documentNoteRepository;
    }

    @Override
    public NoteComment createComment(Note n, String message) {
        NoteComment comment = new NoteComment(n, message, authService.getUsername());
        getSession().save(comment);
        return comment;
    }

    @Override
    public DocumentNote createDocumentNote(Note n, DocumentRevision docRevision, String localId, String longText) {
        UserInfo createdBy = userRepository.getCurrentUser();

        DocumentNote documentNote = new DocumentNote();
        documentNote.setCreatedOn(timeService.currentTimeMillis());
        n.setLastEditedBy(createdBy);
        if (n.getAllEditors() == null) {
            n.setAllEditors(new HashSet<UserInfo>());
        }
        n.getAllEditors().add(createdBy);
        documentNote.setSVNRevision(docRevision.getRevision());
        documentNote.setLongText(longText);

        if (n.getLemma() == null) {
            n.setLemma(Note.createLemmaFromLongText(longText));
        }
        documentNote.setDocument(docRevision.getDocument());
        documentNote.setDocRevision(docRevision);
        documentNote.setLocalId(localId);
        documentNote.setNote(n);
        getSession().save(documentNote);
        getSession().flush();

        documentNoteRepository.removeOrphans(documentNote.getNote().getId());

        return documentNote;
    }

    @Override
    public Note find(String lemma) {
        return getSession().from(note).where(note.lemma.eq(lemma)).uniqueResult(note);
    }

    private void handleEndElement(XMLStreamReader reader, LoopContext data) {
        String localName = reader.getLocalName();

        if (localName.equals("note")) {
            documentNoteRepository.save(data.documentNote);
            data.counter++;
        } else if (localName.equals("lemma")) {
            data.documentNote.getNote().setLemma(data.text);
        } else if (localName.equals("lemma-meaning")) {
            data.documentNote.getNote().setLemmaMeaning(data.text);
        } else if (localName.equals("source")) {
            data.documentNote.getNote().setSources(data.paragraphs);
            data.paragraphs = null;
        } else if (localName.equals("description")) {
            data.documentNote.getNote().setDescription(data.paragraphs);
            data.paragraphs = null;
        } else if (localName.equals("bibliograph")) {
            data.inBib = false;
        }
    }

    private void handleStartElement(XMLStreamReader reader, LoopContext data) {
        String localName = reader.getLocalName();
        if (localName.equals("note")) {
            data.documentNote = new DocumentNote();
            data.documentNote.setNote(new Note());
        } else if (localName.equals("source") || localName.equals("description")) {
            data.paragraphs = new Paragraph();
        }
        if (localName.equals("bibliograph")) {
            data.inBib = true;
            if (reader.getAttributeCount() > 0) {
                data.attr = reader.getAttributeValue(0);
            }
        } else {
            data.inBib = false;
            data.attr = null;
        }
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
                handleEndElement(reader, data);
            } else if (event == XMLStreamConstants.CHARACTERS) {
                if (data.paragraphs == null) {
                    data.text = reader.getText().replaceAll("\\s+", " ");
                } else {
                    String text = reader.getText().replaceAll("\\s+", " ");
                    if (data.inBib) {
                        LinkElement el = new LinkElement(text);
                        if (data.attr != null) {
                            el.setReference(data.attr);
                        }
                        data.paragraphs.addElement(el);
                    } else {
                        data.paragraphs.addElement(new StringElement(text));
                    }

                }
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

    @Override
    public GridDataSource queryDictionary(String searchTerm) {
        Assert.notNull(searchTerm);
        if (!searchTerm.equals("*")) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(termWithNotes.basicForm.containsIgnoreCase(searchTerm));
            builder.or(termWithNotes.meaning.containsIgnoreCase(searchTerm));
            return createGridDataSource(termWithNotes, termWithNotes.basicForm.lower().asc(),
                    false, builder.getValue());
        }
        return createGridDataSource(termWithNotes, termWithNotes.basicForm.lower().asc(), false);
    }

    @Override
    public void remove(DocumentNote documentNoteToBeRemoved, long revision) {
        Assert.notNull(documentNoteToBeRemoved, "note was null");

//        UserInfo createdBy = userRepository.getCurrentUser();
        DocumentNote documentNote = documentNoteToBeRemoved.createCopy();
        documentNote.setCreatedOn(timeService.currentTimeMillis());
//        documentNote.setCreatedBy(createdBy);
        documentNote.setSVNRevision(revision);
        documentNote.setDeleted(true);

        getSession().save(documentNote);
    }

    @Override
    public NoteComment removeComment(String commentId) {
        NoteComment comment = getSession().getById(commentId, NoteComment.class);
        getSession().delete(comment);
        return comment;
    }

    @Override
    public List<Note> findNotes(String lemma) {
        return getSession().from(note).where(note.lemma.eq(lemma)).list(note);
    }
}
