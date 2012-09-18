/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components.document;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.edith.EDITH;
import com.mysema.edith.services.DocumentDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
public class Upload {

    private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    @Inject
    private DocumentDao documentDao;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Property
    private UploadedFile file;

    @Property
    private String path;

    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private Messages messages;

    // TODO : validate that the name has not been taken

    void onSuccess() throws IOException {
        File tempFile = File.createTempFile("upload", null);
        String uploadPath = path == null ? documentRoot : path;
        try {
            file.write(tempFile);
            if (file.getFileName().endsWith(".zip")) {
                documentDao.addDocumentsFromZip(uploadPath, tempFile);
                message = messages.format("documents-stored-msg", file.getFileName());
            } else {
                documentDao.addDocument(uploadPath + "/" + file.getFileName(), tempFile);
                message = messages.format("document-stored-msg", file.getFileName());
            }
        } finally {
            if (!tempFile.delete()) {
                logger.error("Delete of " + tempFile.getAbsolutePath() + " failed");
            }
        }
    }

}
