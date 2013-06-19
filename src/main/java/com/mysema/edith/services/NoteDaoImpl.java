/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import static com.mysema.query.support.Expressions.stringPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import com.mysema.edith.domain.*;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.QueryModifiers;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.path.StringPath;

@Transactional
public class NoteDaoImpl extends AbstractDao<Note> implements NoteDao {

    private static final QNote note = QNote.note;

    private static final QTerm term = QTerm.term;

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

    private JPASubQuery sub(EntityPath<?> entityPath) {
        return new JPASubQuery().from(entityPath);
    }

    private QueryModifiers getModifiers(NoteSearchTO search) {
        Long limit = search.getPerPage();
        Long offset = null;
        if (search.getPage() != null) {
            offset = (search.getPage() - 1) * limit;
        }
        return new QueryModifiers(limit, offset);
    }

    @Override
    public SearchResults<DocumentNote> findDocumentNotes(NoteSearchTO search) {
        return from(documentNote)
              .innerJoin(documentNote.note, note)
              .leftJoin(note.term, term)
              .where(notesQuery(search, false))
              .orderBy(getOrderBy(search, false))
              .restrict(getModifiers(search))
              .listResults(documentNote);
    }

    @Override
    public SearchResults<Note> findNotes(NoteSearchTO search) {
        return from(note)
              .leftJoin(note.term, QTerm.term)
              .where(notesQuery(search, true))
              .orderBy(getOrderBy(search, true))
              .restrict(getModifiers(search))
              .listResults(note);
    }

    private BooleanBuilder notesQuery(NoteSearchTO search, boolean searchNotes) {
        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder docNoteFilter = new BooleanBuilder();

        builder.and(note.deleted.isFalse());

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
                filter.or(documentNote.document.id.in(search.getDocuments()));
            }
            docNoteFilter.and(filter);
        }

        // language
        if (search.getLanguage() != null) {
            builder.and(note.term.language.eq(search.getLanguage()));
        }

        // fulltext
        if (!Strings.isNullOrEmpty(search.getQuery())) {
            QTerm term = QTerm.term;
            BooleanBuilder filter = new BooleanBuilder();
            for (StringPath path : Arrays.asList(note.lemma, note.description, note.sources,
                    note.comments.any().message, term.basicForm, term.meaning)) {
                filter.or(path.containsIgnoreCase(search.getQuery()));
            }
            if (!searchNotes) {
                filter.or(documentNote.shortenedSelection.containsIgnoreCase(search.getQuery()));
            }
            builder.and(filter);
        }

        // shortened selection
        if (!Strings.isNullOrEmpty(search.getShortenedSelection())) {
            docNoteFilter.and(documentNote.shortenedSelection.containsIgnoreCase(search.getShortenedSelection()));
        }

        // lemma
        if (!Strings.isNullOrEmpty(search.getLemma())) {
            builder.and(note.lemma.containsIgnoreCase(search.getLemma()));
        }

        // lemma meaning
        if (!Strings.isNullOrEmpty(search.getLemmaMeaning())) {
            builder.and(note.lemmaMeaning.containsIgnoreCase(search.getLemmaMeaning()));
        }

        // description
        if (!Strings.isNullOrEmpty(search.getDescription())) {
            builder.and(note.description.containsIgnoreCase(search.getDescription()));
        }

        // creators
        if (!isNullOrEmpty(search.getCreators())) {
            BooleanBuilder filter = new BooleanBuilder();
            for (Long userId : search.getCreators()) {
                User user = userDao.getById(userId);
                filter.or(note.allEditors.contains(user));
            }
            builder.and(filter);
        }

        // formats
        if (!isNullOrEmpty(search.getFormats())) {
            builder.and(note.format.in(search.getFormats()));
        }

        // types
        if (!isNullOrEmpty(search.getTypes())) {
            BooleanBuilder filter = new BooleanBuilder();
            for (NoteType type : search.getTypes()) {
                filter.or(note.types.contains(type));
            }
            builder.and(filter);
        }

        // status
        if (search.getStatus() != null) {
            builder.and(note.status.eq(search.getStatus()));
        }

        // created before
        if (search.getCreatedBefore() != null) {
            docNoteFilter.and(documentNote.createdOn.lt(search.getCreatedBefore()));
        }

        // created after
        if (search.getCreatedAfter() != null) {
            docNoteFilter.and(documentNote.createdOn.gt(search.getCreatedAfter()));
        }

        // edited before
        if (search.getEditedBefore() != null) {
            builder.and(note.editedOn.lt(search.getEditedBefore()));
        }

        // edited after
        if (search.getEditedAfter() != null) {
            builder.and(note.editedOn.gt(search.getEditedAfter()));
        }

        if (docNoteFilter.hasValue()) {
            if (searchNotes) {
                JPASubQuery subQuery = sub(documentNote);
                subQuery.where(
                        documentNote.note.eq(note),
                        documentNote.deleted.isFalse(),
                        docNoteFilter);
                builder.and(subQuery.exists());
            } else {
                builder.and(docNoteFilter);
            }
        }

        return builder;
    }

    private static boolean isNullOrEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    private OrderSpecifier<?> getOrderBy(NoteSearchTO searchInfo, boolean searchNotes) {
        ComparableExpressionBase<?> path = null;
        String order = searchInfo.getOrder();
        QTerm term = QTerm.term;
        if (order == null) {
            path = note.lemma;
        } else if (order.contains("term.")) {
            path = stringPath(term, order.substring(1 + order.lastIndexOf('.')));
        } else if (order.startsWith("note.")) {
            path = stringPath(note, order.substring(1 + order.lastIndexOf('.')));
        } else {
            path = stringPath(searchNotes ? note : documentNote, order);
        }
        return searchInfo.isAscending() ? path.asc() : path.desc();
    }

    @Override
    public List<Long> getOrphanIds() {
        return from(note)
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
        persistOrMerge(n);
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
        try {
            return importNotes(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int importNotes(InputStream stream) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(stream);
        } catch (XMLStreamException e) {
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

    @Override
    public NoteComment removeComment(Long commentId) {
        NoteComment comment = find(NoteComment.class, commentId);
        remove(comment);
        comment.getNote().removeComment(comment);
        return comment;
    }

    @Override
    public Note save(Note note) {
        note.setEditedOn(System.currentTimeMillis());
        return persistOrMerge(note);
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
        detach(note);
        note.setId(null);
        note.setDocumentNoteCount(0);
        persist(note);
    }

}
