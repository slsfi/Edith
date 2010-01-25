/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * SubversionService provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface SubversionService {
    
    void add(String svnPath, File file);
    
    Collection<String> getEntries(String svnFolder, long revision);
    
    File getFile(String svnPath, long revision) throws IOException;

    List<Long> getRevisions(String svnPath);

    void remove(String svnPath);

    void update(String svnPath, File file);

    long commit(String svnPath, File file);

}
