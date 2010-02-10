package fi.finlit.edith.ui.services;

import fi.finlit.edith.domain.SelectedText;

/**
 * NoteAdditionFailedException provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("serial")
public class NoteAdditionFailedException extends Exception{
    
    private final SelectedText selectedText;
    
    private final String localId;

    public NoteAdditionFailedException(SelectedText selectedText, String localId) {
        super("Failed to add " + selectedText + " for note #" + localId);
        this.selectedText = selectedText;
        this.localId = localId;
    }

    public SelectedText getSelectedText() {
        return selectedText;
    }

    public String getLocalId() {
        return localId;
    }
    
}
