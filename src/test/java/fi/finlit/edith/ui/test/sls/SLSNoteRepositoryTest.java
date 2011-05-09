/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.sls;

import com.mysema.rdfbean.tapestry.services.RDFBeanModule;

import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.ui.services.DataModule;
import fi.finlit.edith.ui.services.ServiceModule;
import fi.finlit.edith.ui.test.services.NoteRepositoryTest;

@Modules({ SLSServiceTestModule.class, ServiceModule.class, DataModule.class, RDFBeanModule.class })
public class SLSNoteRepositoryTest extends NoteRepositoryTest {

}
