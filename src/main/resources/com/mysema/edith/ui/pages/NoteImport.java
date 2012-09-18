/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.edith.services.NoteDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
public class NoteImport {

    private static final Logger logger = LoggerFactory.getLogger(NoteImport.class);

    @Inject
    private NoteDao noteDao;

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
            int rv = noteDao.importNotes(tempFile);
            message = messages.format("notes-imported-msg", rv);
        } finally {
            if (!tempFile.delete() && !tempFile.delete()) {
                logger.error("Delete of " + tempFile.getAbsolutePath() + " failed");
            }
        }
    }

}
