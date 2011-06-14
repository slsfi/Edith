/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AdminService {
    void removeNotes();

    void removeNotesAndTerms();

    void removeNotesAndTermsAndDocuments();

}
