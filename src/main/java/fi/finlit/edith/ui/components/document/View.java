package fi.finlit.edith.ui.components.document;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.ui.services.DocumentRenderer;

/**
 * ViewPanel provides
 *
 * @author tiwe
 * @version $Id$
 */
public class View {
    
    @Inject
    private DocumentRepository documentRepo;

    @Inject
    private DocumentRenderer renderer;
    
    private File docFile;
    
    @Parameter
    private String svnPath;
    
    @Parameter
    private long revision;
    
    @SetupRender
    void setupRender() throws IOException {
        docFile = documentRepo.getDocumentFile(svnPath, revision);
    }

    @BeginRender
    void beginRender(MarkupWriter writer) throws Exception {
        renderer.render(docFile, writer);
    }
}
