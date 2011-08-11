package fi.finlit.edith.ui.services;

import java.util.List;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;

public interface DocumentNoteDao extends Dao<DocumentNote, Long> {

    /**
     * @param docNote
     */
    @CommitAfter
    void remove(DocumentNote docNote);

    /**
     * @param docNote
     * @return
     */
    @CommitAfter
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
     * @param documentRevision
     * @return
     */
    List<DocumentNote> getPublishableNotesOfDocument(Document document);

    /**
     * @param note
     * @return
     */
    int getDocumentNoteCount(Note note);

    long getNoteCountForDocument(Long id);

}
