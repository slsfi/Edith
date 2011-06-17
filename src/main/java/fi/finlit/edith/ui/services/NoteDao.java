package fi.finlit.edith.ui.services;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;

import fi.finlit.edith.dto.NoteSearchInfo;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteComment;

public interface NoteDao extends Dao<Note, Long> {

    /**
     * Creates a comment for the given note.
     *
     * @param concept
     * @param message
     */
    NoteComment createComment(Note note, String message);

    /**
     * Create a new document note for the given DocumentRevision with the given local id, lemma and long text
     *
     * @param docRevision
     * @param localId
     * @param longText
     * @return
     */
    DocumentNote createDocumentNote(Note note, Document document, String longText, int position);

    DocumentNote createDocumentNote(DocumentNote documentNote, Note note, Document document, String longText, int position);

    /**
     * @param lemma
     * @return
     */
    Note find(String lemma);

    /**
     * Import notes from the given file
     *
     * @param file
     * @throws Exception
     */
    int importNotes(File file);

    /**
     * Query for notes with the given search term
     *
     * @param searchTerm
     * @return
     */
    GridDataSource queryDictionary(String searchTerm);

    /**
     * Removes the document note permanently
     *
     * @param note
     */
    void removePermanently(DocumentNote note);

    /**
     * Removes a NoteComment based on its id. Returns the deleted comment.
     *
     * @param commentId
     * @return
     */
    NoteComment removeComment(Long commentId);

    /**
     * Finds all the Notes based on a lemma.
     * @param lemma
     * @return
     */
    List<Note> findNotes(String lemma);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryPersons(String searchTerm);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryPlaces(String searchTerm);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryNotes(String searchTerm);

    /**
     * @return
     */
    List<Note> getOrphans();

    /**
     * @return
     */
    List<Long> getOrphanIds();

    GridDataSource findNotes(NoteSearchInfo search);

    /**
     * @param editedNote
     */
    void save(Note editedNote);

    /**
     * Remove notes
     *
     * @param notes
     */
    void removeNotes(Collection<Note> notes);

    /**
     * Remove note
     *
     * @param note
     */
    void removeNote(Note note);

    /**
     * @param id
     * @return
     */
    NoteComment getCommentById(Long id);

    DocumentNote createDocumentNote(Note note, Document document, String longText);

}
