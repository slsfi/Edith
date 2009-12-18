package fi.finlit.editio.ui.services;

import static fi.finlit.editio.domain.QDocument.document;

import java.util.List;

import fi.finlit.editio.domain.Document;
import fi.finlit.editio.domain.DocumentRepository;

/**
 * DocumentRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryImpl extends AbstractRepository<Document> implements DocumentRepository{

    protected DocumentRepositoryImpl() {
        super(document);
    }

    @Override
    public Document getDocumentForPath(String svnPath) {
        return getSession().from(document).where(document.svnPath.eq(svnPath))
            .uniqueResult(document);
    }

    @Override
    public List<Document> getDocumentsOfFolder(String svnFolder) {
        return getSession().from(document).where(document.svnPath.startsWith(svnFolder))
            .list(document);
    }

}
