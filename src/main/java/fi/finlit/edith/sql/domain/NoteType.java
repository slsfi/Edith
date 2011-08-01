package fi.finlit.edith.sql.domain;

import javax.persistence.Table;

@Table(name = "note_type")
public enum NoteType {
    WORD_EXPLANATION,
    LITERARY,
    HISTORICAL,
    DICTUM,
    CRITIQUE,
    TITLE,
    TRANSLATION,
    REFERENCE
}
