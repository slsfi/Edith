package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import fi.finlit.edith.domain.NoteRevision;

@SuppressWarnings("unused")
public class Metadata {

    @Property
    @Parameter(required = true)
    private NoteRevision note;


}
