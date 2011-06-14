/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components.document;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.DocumentRepository;

@SuppressWarnings("unused")
public class Upload {

    private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    @Inject
    private DocumentRepository documentRepository;

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
                documentRepository.addDocumentsFromZip(uploadPath, tempFile);
                message = messages.format("documents-stored-msg", file.getFileName());
            } else {
                documentRepository.addDocument(uploadPath + "/" + file.getFileName(), tempFile);
                message = messages.format("document-stored-msg", file.getFileName());
            }
        } finally {
            if (!tempFile.delete()) {
                logger.error("Delete of " + tempFile.getAbsolutePath() + " failed");
            }
        }
    }

}
