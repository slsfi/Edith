/**
 *
 */
package fi.finlit.edith.domain;

import java.io.Serializable;
import java.util.Comparator;


public final class NoteComparator implements Comparator<Note>, Serializable {

    private static final long serialVersionUID = 1172304280333678242L;

    @Override
    public int compare(Note o1, Note o2) {
        return o1.getLemma().compareTo(o2.getLemma());
    }
}