package fi.finlit.edith.ui.services;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;

import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;

public interface DocumentNoteDao extends Dao<DocumentNote, Long> {

    /**
     * @param docNote
     */
    void remove(DocumentNote docNote);

    /**
     * @param documentNoteId
     */
    void remove(Long documentNoteId);

    /**
     * @param docNote
     * @return
     */
    DocumentNote save(DocumentNote docNote);

//    /**
//     * Get the NoteRevision with the given local id in the scope of the given document revision
//     *
//     * @param document
//     * @param documentRevision
//     * @param localId
//     * @return
//     */
//    @Nullable
//    DocumentNote getByLocalId(DocumentRevision docRevision, String localId);

    /**
     * Get the note revisions of the given document revision
     *
     * @param document
     * @param revision
     * @return
     */
    List<DocumentNote> getOfDocument(Document document);

    /**
     * Get the document notes of the given note.
     *
     * @param noteId
     * @return
     */
    List<DocumentNote> getOfNote(Long noteId);

    /**
     * @param notes
     * @return
     */
    List<DocumentNote> getOfNotes(Collection<Note> notes);

    /**
     * Get the DocumentNotes of the given Person.
     *
     * @param personId
     * @return
     */
    List<DocumentNote> getOfPerson(Long personId);

    /**
     * Get the DocumentNotes of the given Place.
     *
     * @param personId
     * @return
     */
    List<DocumentNote> getOfPlace(Long placeId);

    /**
     * Query for notes matching the given search term
     *
     * @param searchTem
     * @return
     */
    GridDataSource queryNotes(String searchTem);

    /**
     * Returns all the document notes attached to the term.
     *
     * @param termId
     * @return
     */
    List<DocumentNote> getOfTerm(Long termId);

//    /**
//     * Saves the document note with the backing note copied as a new one.
//     *
//     * @param docNote
//     * @return
//     */
//    DocumentNote saveAsCopy(DocumentNote docNote);

    /**
     * Returns the DocumentNotes with the same given Note in the given Document.
     *
     * @param noteId
     * @param documentId
     * @return
     */
    List<DocumentNote> getOfNoteInDocument(Long noteId, Long documentId);

    /**
     * @param documentRevision
     * @return
     */
    List<DocumentNote> getPublishableNotesOfDocument(Document document);

    /**
     * @return
     */
    // FIXME: WTF?
    List<DocumentNote> getNotesLessDocumentNotes();

    /**
     * @param note
     * @return
     */
    int getDocumentNoteCount(Note note);

    long getNoteCountForDocument(Long id);

}
