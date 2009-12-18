package fi.finlit.editio.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.editio.EDITIO;

/**
 * Document provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public class Document {
    
    @Predicate
    private String description;

    @Predicate
    private String svnPath;

    @Predicate
    private String title;

    public String getDescription() {
        return description;
    }

    public String getSvnPath() {
        return svnPath;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setSvnPath(String svnPath) {
        this.svnPath = svnPath;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

}
