/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.mysema.edith.domain.Document;
import com.mysema.edith.dto.FileItemWithDocumentId;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.ui.pages.document.Annotate;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
@Import(library = { "classpath:js/jquery-1.5.1.min.js",
        "classpath:js/jquery-ui-1.8.12.custom.min.js", "classpath:js/jquery.cookie.js",
        "deleteDialog.js", "classpath:js/jquery.dynatree.js", "classpath:js/url-encoder.js" }, stylesheet = { "context:styles/dynatree/skin/ui.dynatree.css" })
public class Documents {

    @Inject
    private DocumentDao documentDao;

    @Property
    private Document document;

    @SessionState(create = false)
    private Collection<Document> selectedDocuments;

    private Collection<Document> selectedForDeletion;

    private Collection<Document> toBeDeleted;

    private boolean removeSelected;

    @Inject
    private PageRenderLinkSource linkSource;

    void onActivate() {
        selectedForDeletion = new HashSet<Document>();
        if (selectedDocuments == null) {
            selectedDocuments = new HashSet<Document>();
        }
    }

    public boolean isDocumentSelected() {
        return selectedDocuments.contains(document);
    }

    public void setDocumentSelected(boolean selected) {
        if (selected) {
            selectedDocuments.add(document);
        } else {
            selectedDocuments.remove(document);
        }
    }

    void onSelectedFromRemoveSelected() {
        removeSelected = true;
    }

    void onSuccessFromDocumentsForm() {
        if (removeSelected) {
            documentDao.removeAll(selectedForDeletion);
        }
    }

    public boolean isSelectedForDeletion() {
        return selectedForDeletion.contains(document);
    }

    public void setSelectedForDeletion(boolean selected) {
        if (selected) {
            selectedForDeletion.add(document);
        } else {
            selectedForDeletion.remove(document);
        }
    }

    TextStreamResponse onJson(@RequestParameter(value = "path", allowBlank = true) String path,
            @RequestParameter(value = "id", allowBlank = true) Long id) {
        Gson gson = new Gson();
        List<FileItemWithDocumentId> fileItems = documentDao.fromPath(path, id);
        return new TextStreamResponse("application/json", gson.toJson(fileItems));
    }

    public String getDocumentsAjaxURL() {
        return linkSource.createPageRenderLink(Documents.class).toString();
    }

    public String getAnnotateURL() {
        return linkSource.createPageRenderLinkWithContext(Annotate.class).toString();
    }

    public String getDeleteDocumentURL() {
        return linkSource.createPageRenderLink(Documents.class).toString() + ".deletedocument";
    }

    public String getRenameDocumentURL() {
        return linkSource.createPageRenderLink(Documents.class).toString() + ".renamedocument";
    }

    public void onActionFromDeleteDocument(Long id) {
        documentDao.remove(id);
    }

    public void onActionFromRenameDocument(Long id, String newName) {
        documentDao.rename(id, newName);
    }

}
