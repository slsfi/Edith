package fi.finlit.edith.ui.test.sls;

import com.mysema.rdfbean.tapestry.services.RDFBeanModule;

import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.ui.services.DataModule;
import fi.finlit.edith.ui.services.ServiceModule;
import fi.finlit.edith.ui.test.services.DocumentNoteRepositoryTest;

@Modules({ SLSServiceTestModule.class, ServiceModule.class, DataModule.class, RDFBeanModule.class })
public class SLSDocumentNoteRepositoryTest extends DocumentNoteRepositoryTest {

}
