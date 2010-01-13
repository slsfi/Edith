package fi.finlit.edith.ui.services;

import org.springframework.transaction.annotation.Transactional;

/**
 * AdminService provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface AdminService {
    
    void removeNotes();

    void removeNotesAndTerms();

}
