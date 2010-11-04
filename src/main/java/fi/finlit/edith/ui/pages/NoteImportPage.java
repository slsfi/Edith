/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.ui.services.NoteRepository;

/**
 * NoteImportPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class NoteImportPage {

    private static final Logger logger = LoggerFactory.getLogger(NoteImportPage.class);

    @Inject
    private NoteRepository noteRepository;

    @Property
    private UploadedFile file;

    @Inject
    private Messages messages;

    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    void onActivate() {
    }

    public void onSuccess() throws IOException {
        File tempFile = File.createTempFile("upload", null);
        try {
            file.write(tempFile);
            int rv = noteRepository.importNotes(tempFile);
            message = messages.format("notes-imported-msg", rv);
        } finally {
            if (!tempFile.delete() && !tempFile.delete()) {
                logger.error("Delete of " + tempFile.getAbsolutePath() + " failed");
            }
        }
    }

}
