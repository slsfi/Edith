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
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.DocumentRepository;

/**
 * Upload provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class Upload {

    private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    @Inject
    private DocumentRepository documentRepo;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Property
    private UploadedFile file;

    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private Messages messages;

    // TODO : validate that the name has not been taken

    void onSuccess() throws IOException {
        File tempFile = File.createTempFile("upload", null);
        try {
            file.write(tempFile);
            String path = documentRoot + "/" + file.getFileName();
            documentRepo.addDocument(path, tempFile);
            message = messages.format("document-stored-msg", file.getFileName());
        } finally {
            if (!tempFile.delete()) {
                logger.error("Delete of " + tempFile.getAbsolutePath() + " failed");
            }
        }
    }

}
