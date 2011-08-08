package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.sql.domain.QDocumentNote.documentNote;
import static fi.finlit.edith.sql.domain.QNote.note;
import static fi.finlit.edith.sql.domain.QPerson.person;
import static fi.finlit.edith.sql.domain.QPlace.place;
import static fi.finlit.edith.sql.domain.QTerm.term;

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

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLSubQuery;
import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.path.StringPath;
import com.mysema.query.types.template.BooleanTemplate;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.dto.NoteSearchInfo;
import fi.finlit.edith.dto.OrderBy;
import fi.finlit.edith.dto.UserInfo;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.LinkElement;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteComment;
import fi.finlit.edith.sql.domain.NoteType;
import fi.finlit.edith.sql.domain.Paragraph;
import fi.finlit.edith.sql.domain.StringElement;
import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.sql.domain.UrlElement;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.ServiceException;
import fi.finlit.edith.ui.services.UserDao;

public class NoteDaoImpl extends AbstractDao<Note> implements NoteDao {

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

    public NoteDaoImpl(
            @Inject UserDao userDao,
            @Inject AuthService authService,
            @Inject @Symbol(EDITH.EXTENDED_TERM) boolean extendedTerm) {
        this.userDao = userDao;
        this.authService = authService;
        this.extendedTerm = extendedTerm;
    }

    @Override
    public Collection<Note> getAll() {
        return query().from(note).list(note);
    }

    @Override
    public Note getById(Long id) {
        return (Note) getSession().get(Note.class, id);
    }

    @Override
    public NoteComment createComment(Note note, String message) {
        NoteComment comment = new NoteComment(note, message, authService.getUsername());
        note.addComment(comment);
        comment.setCreatedAt(new DateTime());
        getSession().save(comment);
        return comment;
    }

    private JPQLSubQuery sub(EntityPath<?> entityPath) {
        return new JPQLSubQuery().from(entityPath);
    }

    @Override
    public List<Note> listNotes(NoteSearchInfo search) {
        return query().from(note).where(notesQuery(search)).orderBy(getOrderBy(search)).list(note);
    }

    @Override
    public GridDataSource findNotes(NoteSearchInfo search) {
        return createGridDataSource(note, getOrderBy(search), false, notesQuery(search).getValue());
    }

    private BooleanBuilder notesQuery(NoteSearchInfo search) {
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
        // or documents from selection
        else if (!search.getDocuments().isEmpty() && !search.isIncludeAllDocs()) {
            JPQLSubQuery subQuery = sub(documentNote);
            subQuery.where(documentNote.note.eq(note),
                    documentNote.document.in(search.getDocuments()),
                    documentNote.deleted.eq(false));
            builder.and(subQuery.exists());
        }

        //language
        if (search.getLanguage() != null) {
            builder.and(note.term.language.eq(search.getLanguage()));
        }

        // fulltext
        if (StringUtils.isNotBlank(search.getFullText())) {
            BooleanBuilder filter = new BooleanBuilder();
            for (StringPath path : Arrays.asList(note.lemma,
                                                 note.term.basicForm,
                                                 note.term.meaning,
                                                 note.description,
                                                 note.sources,
                                                 note.comments.any().message)) {
                filter.or(path.containsIgnoreCase(search.getFullText()));
            }
            builder.and(filter);
        }

        // creators
        if (!search.getCreators().isEmpty()) {
            BooleanBuilder filter = new BooleanBuilder();
            Collection<String> usernames = new ArrayList<String>(search.getCreators().size());
            for (UserInfo userInfo : search.getCreators()) {
                filter.or(note.allEditors.contains(
                        userDao.getByUsername(userInfo.getUsername())));
                usernames.add(userInfo.getUsername());
            }
            // FIXME This is kind of useless except that we have broken data in production.
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
                /*
                 * FIXME: Temporary quickfix. Should be fixed in Querydsl at some point.
                 * Reported issue: https://bugs.launchpad.net/querydsl/+bug/800698
                 */
//                filter.or(note.types.contains(type));
                filter.or(BooleanTemplate.create("{0} in elements({1})", new ConstantImpl<String>(type.name()), note.types));
            }
            builder.and(filter);
        }

        return builder;
    }

    @Override
    public GridDataSource queryNotes(String searchTerm) {
        Assert.notNull(searchTerm);
        BooleanBuilder builder = new BooleanBuilder();
        if (!searchTerm.equals("*")) {
            for (StringPath path : Arrays.asList(
                    note.lemma,
                    note.term.basicForm,
                    note.term.meaning)) {
                // ,
                // documentNote.description, FIXME
                // note.subtextSources)
                builder.or(path.containsIgnoreCase(searchTerm));
            }
        }

        return createGridDataSource(note, note.term.basicForm.lower().asc(), false, builder.getValue());
    }


    @Override
    public NoteComment getCommentById(Long id) {
        return (NoteComment) getSession().get(NoteComment.class, id);
    }


    private OrderSpecifier<?> getOrderBy(NoteSearchInfo searchInfo) {
        ComparableExpressionBase<?> comparable = null;
        OrderBy orderBy = searchInfo.getOrderBy() == null ? OrderBy.LEMMA : searchInfo.getOrderBy();
        switch (orderBy) {
        case KEYTERM:
            comparable = note.term.basicForm;
        case DATE:
            comparable = note.editedOn;
            break;
        case USER:
            comparable = note.lastEditedBy.username.toLowerCase();
            break;
        case STATUS:
            comparable = note.status;//.ordinal();
            break;
        default:
            comparable = note.lemma.toLowerCase();
            break;
        }
        return searchInfo.isAscending() ? comparable.asc() : comparable.desc();
    }

    @Override
    public List<Note> getOrphans() {
        return query()
            .from(note)
            .where(sub(documentNote).where(documentNote.note.eq(note)).notExists())
            .list(note);
    }

    @Override
    public List<Long> getOrphanIds() {
        return query().from(note)
                .where(sub(documentNote).where(documentNote.note.eq(note)).notExists())
                .list(note.id);
    }

    @Override
    public DocumentNote createDocumentNote(Note n, Document document,
            String longText) {
        return createDocumentNote(new DocumentNote(), n, document,
                longText, 0);
    }

    @Override
    public DocumentNote createDocumentNote(Note n, Document document,
            String longText, int position) {
        return createDocumentNote(new DocumentNote(), n, document,
                longText, position);
    }

    @Override
    //TODO Create variants for SKS/SLS
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

        String abbreviation = StringUtils.abbreviate(longText, 85);

        //Extended term version gets the lemma also to basicTerm automatically
        if (extendedTerm && n.getTerm() != null && n.getTerm().getBasicForm() == null) {
            n.getTerm().setBasicForm(abbreviation);
        }
        //And to the short version
        if (extendedTerm && documentNote.getShortenedSelection() == null) {
            documentNote.setShortenedSelection(abbreviation);
        }

        documentNote.setDocument(document);
        documentNote.setNote(n);
        n.incDocumentNoteCount();
        documentNote.setPosition(position);
        getSession().save(n);
        getSession().save(documentNote);

        return documentNote;
    }

    @Override
    public Note find(String lemma) {
        return query().from(note).where(note.lemma.eq(lemma)).uniqueResult(note);
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

    @Override
    public GridDataSource queryDictionary(String searchTerm) {
        // FIXME!!!!
        Assert.notNull(searchTerm);
        if (!searchTerm.equals("*")) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(term.basicForm.containsIgnoreCase(searchTerm));
            builder.or(term.meaning.containsIgnoreCase(searchTerm));
            return createGridDataSource(term, term.basicForm.lower().asc(),
                    false, builder.getValue());
        }
        return createGridDataSource(term, term.basicForm.lower().asc(), false, null);
    }

    @Override
    public GridDataSource queryPersons(String searchTerm) {
        Assert.notNull(searchTerm);
        if (!searchTerm.equals("*")) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(person.normalizedForm.first.containsIgnoreCase(searchTerm));
            builder.or(person.normalizedForm.last.containsIgnoreCase(searchTerm));
            return createGridDataSource(person, person.normalizedForm.last.lower().asc(), false,
                    builder.getValue());
        }
        return createGridDataSource(person, person.normalizedForm.last.asc(), false, null);
    }

    @Override
    public GridDataSource queryPlaces(String searchTerm) {
        Assert.notNull(searchTerm);
        if (!searchTerm.equals("*")) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(place.normalizedForm.last.containsIgnoreCase(searchTerm));
            return createGridDataSource(place, place.normalizedForm.last.lower().asc(), false,
                    builder.getValue());
        }
        return createGridDataSource(place, place.normalizedForm.last.asc(), false, null);
    }

    @Override
    public void removePermanently(DocumentNote documentNote) {
        Note n = documentNote.getNote();
        getSession().delete(documentNote);
        n.decDocumentNoteCount();
        save(n);
    }

    @Override
    public NoteComment removeComment(Long commentId) {
        // FIXME: Do differently with Hibernate!
        NoteComment comment = (NoteComment) getSession().get(NoteComment.class, commentId);
        getSession().delete(comment);
        comment.getNote().removeComment(comment);
        return comment;
    }

    @Override
    public List<Note> findNotes(String lemma) {
        return query().from(note).where(note.lemma.eq(lemma)).list(note);
    }

    @Override
    public void save(Note note) {
        getSession().save(note);
    }

    @Override
    public void removeNote(Note note) {
        getSession().delete(note);
    }

    @Override
    public void removeNotes(Collection<Note> notes) {
        for (Note note : notes){
            removeNote(note);
        }
    }

}
