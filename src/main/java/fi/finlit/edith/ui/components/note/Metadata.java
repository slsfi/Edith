package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.annotations.Parameter;

import fi.finlit.edith.domain.NoteRevision;

public class Metadata {
    @Parameter(required = true)
    private NoteRevision note;

}
