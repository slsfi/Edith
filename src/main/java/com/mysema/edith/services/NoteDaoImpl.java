/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.LinkElement;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.Paragraph;
import com.mysema.edith.domain.QDocumentNote;
import com.mysema.edith.domain.QNote;
import com.mysema.edith.domain.QTerm;
import com.mysema.edith.domain.StringElement;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.UrlElement;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.edith.dto.OrderBy;
import com.mysema.edith.dto.UserTO;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLSubQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.path.StringPath;

@Transactional
public class NoteDaoImpl extends AbstractDao<Note> implements NoteDao {

    private static final QNote note = QNote.note;

    private static final QDocumentNote documentNote = QDocumentNote.documentNote;

    private static final class LoopContext {
        private Note note;
        private String text;
        private Paragraph paragraphs;
        private int counter;
        private boolean inBib;
        private boolean inA;
        private String reference;
        private String url;

        private LoopContext() {
            note = null;
            text = null;
            counter = 0;
        }
    }

    private final UserDao userDao;

    private final AuthService authService;

    private final boolean extendedTerm;

    @Inject
    public NoteDaoImpl(UserDao userDao, AuthService authService,
            @Named(EDITH.EXTENDED_TERM) boolean extendedTerm) {
        this.userDao = userDao;
        this.authService = authService;
        this.extendedTerm = extendedTerm;
    }

    @Override
    public Note getById(Long id) {
        return find(Note.class, id);
    }

    @Override
    public NoteComment createComment(Note note, String message) {
        NoteComment comment = new NoteComment(note, message, authService.getUsername());
        note.addComment(comment);
        comment.setCreatedAt(new DateTime());
        persist(comment);
        return comment;
    }

    private JPQLSubQuery sub(EntityPath<?> entityPath) {
        return new JPQLSubQuery().from(entityPath);
    }

//    @Override
//    public GridDataSource findNotes(NoteSearchInfo search) {
//        return createGridDataSource(note, getOrderBy(search), false, notesQuery(search).getValue());
//    }

    private BooleanBuilder notesQuery(NoteSearchTO search) {
        BooleanBuilder builder = new BooleanBuilder();

        // only orphans
        if (search.isOrphans() && !search.isIncludeAllDocs()) {
            builder.and(note.documentNoteCount.eq(0));
        }
        // or all, including orphans
        else if (search.isOrphans() && search.isIncludeAllDocs()) {
            builder.and(note.documentNoteCount.goe(0));
        }
        // or all, without orphans
        else if (!search.isOrphans() && search.isIncludeAllDocs()) {
            builder.and(note.documentNoteCount.gt(0));
        }
        // or documents and paths from selection
        else if (!search.isIncludeAllDocs()
                && (!search.getPaths().isEmpty() || !search.getDocuments().isEmpty())) {

            BooleanBuilder filter = new BooleanBuilder();
            for (String path : search.getPaths()) {
                filter.or(documentNote.document.path.startsWith(path));
            }
            if (!search.getDocuments().isEmpty()) {
                filter.or(documentNote.document.in(search.getDocuments()));
            }

            JPQLSubQuery subQuery = sub(documentNote);
            subQuery.where(documentNote.note.eq(note), documentNote.deleted.eq(false), filter);

            builder.and(subQuery.exists());
        }

        // language
        if (search.getLanguage() != null) {
            builder.and(note.term.language.eq(search.getLanguage()));
        }

        // fulltext
        if (Strings.isNullOrEmpty(search.getFullText())) {
            BooleanBuilder filter = new BooleanBuilder();
            for (StringPath path : Arrays.asList(note.lemma, note.description, note.sources,
                    note.comments.any().message)) {
                filter.or(path.containsIgnoreCase(search.getFullText()));
            }
            // NOTE : this needs to be separate because otherwise there is an
            // implicit inner join to term, which is an optional property
            QTerm term = QTerm.term;
            filter.or(sub(term).where(
                    term.eq(note.term),
                    term.basicForm.containsIgnoreCase(search.getFullText()).or(
                            term.meaning.containsIgnoreCase(search.getFullText()))).exists());
            builder.and(filter);
        }

        // creators
        if (!search.getCreators().isEmpty()) {
            BooleanBuilder filter = new BooleanBuilder();
            Collection<String> usernames = new ArrayList<String>(search.getCreators().size());
            for (UserTO userInfo : search.getCreators()) {
                filter.or(note.allEditors.contains(userDao.getByUsername(userInfo.getUsername())));
                usernames.add(userInfo.getUsername());
            }
            // FIXME This is kind of useless except that we have broken data in
            // production.
            filter.or(note.lastEditedBy.username.in(usernames));
            builder.and(filter);
        }

        // formats
        if (!search.getNoteFormats().isEmpty()) {
            builder.and(note.format.in(search.getNoteFormats()));
        }

        // types
        if (!search.getNoteTypes().isEmpty()) {
            BooleanBuilder filter = new BooleanBuilder();
            for (NoteType type : search.getNoteTypes()) {
                filter.or(note.types.contains(type));
            }
            builder.and(filter);
        }

        return builder;
    }

//    @Override
//    public GridDataSource queryNotes(String searchTerm) {
//        Assert.notNull(searchTerm, "searchTerm");
//        BooleanBuilder builder = new BooleanBuilder();
//        if (!searchTerm.equals("*")) {
//            for (StringPath path : Arrays
//                    .asList(note.lemma, note.term.basicForm, note.term.meaning)) {
//                // ,
//                // documentNote.description, FIXME
//                // note.subtextSources)
//                builder.or(path.containsIgnoreCase(searchTerm));
//            }
//        }
//        builder.and(note.deleted.isFalse());
//
//        // return createGridDataSource(note, note.term.basicForm.lower().asc(),
//        // false, builder.getValue());
//        return createGridDataSource(note, note.lemma.asc(), false, builder.getValue());
//    }

    private OrderSpecifier<?> getOrderBy(NoteSearchTO searchInfo) {
        ComparableExpressionBase<?> comparable = null;
        OrderBy orderBy = searchInfo.getOrderBy() == null ? OrderBy.LEMMA : searchInfo.getOrderBy();
        switch (orderBy) {
        case KEYTERM:
            comparable = note.term.basicForm;
            break;
        case DATE:
            comparable = note.editedOn;
            break;
        case USER:
            comparable = note.lastEditedBy.username.toLowerCase();
            break;
        case STATUS:
            comparable = note.status;// .ordinal();
            break;
        default:
            comparable = note.lemma.toLowerCase();
            break;
        }
        return searchInfo.isAscending() ? comparable.asc() : comparable.desc();
    }

    @Override
    public List<Long> getOrphanIds() {
        return query().from(note)
                .where(sub(documentNote).where(documentNote.note.eq(note)).notExists())
                .list(note.id);
    }

    @Override
    public DocumentNote createDocumentNote(Note n, Document document, String longText) {
        return createDocumentNote(new DocumentNote(), n, document, longText, 0);
    }

    @Override
    // TODO Create variants for SKS/SLS
    public DocumentNote createDocumentNote(DocumentNote documentNote, Note n, Document document,
            String longText, int position) {
        User createdBy = userDao.getCurrentUser();

        long currentTime = System.currentTimeMillis();
        documentNote.setCreatedOn(currentTime);
        n.setEditedOn(currentTime);

        n.setLastEditedBy(createdBy);
        n.addEditor(createdBy);

        documentNote.setFullSelection(longText);

        String createdLemma = Note.createLemmaFromLongText(longText);
        if (n.getLemma() == null && !extendedTerm) {
            n.setLemma(createdLemma);
        }

        String abbreviation = longText;
        if (abbreviation.length() > 85) {
            abbreviation = abbreviation.substring(0, 85);
        }

        // Extended term version gets the lemma also to basicTerm automatically
        if (extendedTerm && n.getTerm() != null && n.getTerm().getBasicForm() == null) {
            n.getTerm().setBasicForm(abbreviation);
        }
        // And to the short version
        if (extendedTerm && documentNote.getShortenedSelection() == null) {
            documentNote.setShortenedSelection(abbreviation);
        }

        documentNote.setDocument(document);
        documentNote.setNote(n);
        n.incDocumentNoteCount();
        documentNote.setPosition(position);
        persist(n);
        persist(documentNote);
        return documentNote;
    }

    private void handleEndElement(XMLStreamReader reader, LoopContext data) {
        String localName = reader.getLocalName();

        if (localName.equals("note")) {
            data.note.setLastEditedBy(userDao.getCurrentUser());
            data.note.setEditedOn(System.currentTimeMillis());
            save(data.note);
            data.counter++;
        } else if (localName.equals("lemma")) {
            data.note.setLemma(data.text);
        } else if (localName.equals("lemma-meaning")) {
            data.note.setLemmaMeaning(data.text);
        } else if (localName.equals("source")) {
            data.note.setSources(data.paragraphs.toString());
            data.paragraphs = null;
        } else if (localName.equals("description")) {
            data.note.setDescription(data.paragraphs.toString());
            data.paragraphs = null;
        } else if (localName.equals("bibliograph")) {
            data.inBib = false;
            data.reference = null;
        } else if (localName.equals("a")) {
            data.inA = false;
            data.url = null;
        }
    }

    private void handleStartElement(XMLStreamReader reader, LoopContext data) {
        String localName = reader.getLocalName();
        if (localName.equals("note")) {
            data.note = new Note();
            if (extendedTerm) {
                data.note.setTerm(new Term());
            }
        } else if (localName.equals("source") || localName.equals("description")) {
            data.paragraphs = new Paragraph();
        }
        if (localName.equals("bibliograph")) {
            data.inBib = true;
            if (reader.getAttributeCount() > 0) {
                data.reference = reader.getAttributeValue(0);
            }
        } else if (localName.equals("a")) {
            data.inA = true;
            if (reader.getAttributeCount() > 0) {
                data.url = reader.getAttributeValue(0);
            }
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
                        if (data.reference != null) {
                            el.setReference(data.reference);
                        }
                        data.paragraphs.addElement(el);
                    } else if (data.inA) {
                        UrlElement el = new UrlElement(text);
                        if (data.url != null) {
                            el.setUrl(data.url);
                        }
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

//    @Override
//    public GridDataSource queryDictionary(String searchTerm) {
//        // FIXME!!!!
//        Assert.notNull(searchTerm, "searchTerm");
//        if (!searchTerm.equals("*")) {
//            BooleanBuilder builder = new BooleanBuilder();
//            builder.or(term.basicForm.containsIgnoreCase(searchTerm));
//            builder.or(term.meaning.containsIgnoreCase(searchTerm));
//            return createGridDataSource(term, term.basicForm.lower().asc(), false,
//                    builder.getValue());
//        }
//        return createGridDataSource(term, term.basicForm.lower().asc(), false, null);
//    }
//
//    @Override
//    public GridDataSource queryPersons(String searchTerm) {
//        Assert.notNull(searchTerm, "searchTerm");
//        if (!searchTerm.equals("*")) {
//            BooleanBuilder builder = new BooleanBuilder();
//            builder.or(person.normalized.first.containsIgnoreCase(searchTerm));
//            builder.or(person.normalized.last.containsIgnoreCase(searchTerm));
//            return createGridDataSource(person, person.normalized.last.lower().asc(), false,
//                    builder.getValue());
//        }
//        return createGridDataSource(person, person.normalized.last.asc(), false, null);
//    }
//
//    @Override
//    public GridDataSource queryPlaces(String searchTerm) {
//        Assert.notNull(searchTerm, "searchTerm");
//        if (!searchTerm.equals("*")) {
//            BooleanBuilder builder = new BooleanBuilder();
//            builder.or(place.normalized.last.containsIgnoreCase(searchTerm));
//            return createGridDataSource(place, place.normalized.last.lower().asc(), false,
//                    builder.getValue());
//        }
//        return createGridDataSource(place, place.normalized.last.asc(), false, null);
//    }

    @Override
    public NoteComment removeComment(Long commentId) {
        // FIXME: Do differently with Hibernate!
        NoteComment comment = find(NoteComment.class, commentId);
        remove(comment);
        comment.getNote().removeComment(comment);
        return comment;
    }

    @Override
    public Note save(Note note) {
        note.setEditedOn(System.currentTimeMillis());
        if (note.getId() != null) {
            return merge(note);
        }
        persist(note);
        return note;
    }

    @Override
    public void remove(Note note) {
        note.setDeleted(true);
        save(note);
    }

    @Override
    public void remove(Long id) {
        Note note = find(Note.class, id);
        if (note != null) {
            remove(note);
        }
    }

    @Override
    public void removeNotes(Collection<Note> notes) {
        for (Note note : notes) {
            remove(note);
        }
    }

    @Override
    public void saveAsNew(Note note) {
//        EntityManager em = getEntityManager();
//        em.unwrap(Session.class).evict(note);
        evict(note);
        note.setId(null);
        note.setDocumentNoteCount(0);
        persist(note);
    }

}
