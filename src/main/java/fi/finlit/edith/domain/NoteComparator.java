/**
 * 
 */
package fi.finlit.edith.domain;

import java.io.Serializable;
import java.util.Comparator;


public final class NoteComparator implements Comparator<NoteRevision>, Serializable {
    
    private static final long serialVersionUID = 1172304280333678242L;

    @Override
    public int compare(NoteRevision o1, NoteRevision o2) {
        return o1.getLemma().compareTo(o2.getLemma());
    }
}