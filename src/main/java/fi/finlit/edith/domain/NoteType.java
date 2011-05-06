package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

@ClassMapping
public enum NoteType {
    /**
     *
     */
    WORD_EXPLANATION,

    /**
     *
     */
    LITERARY,

    /**
     *
     */
    HISTORICAL,

    /**
     *
     */
    DICTUM,

    /**
     *
     */
    CRITIQUE,
    
    /**
     * 
     */
    TITLE,
    
    /**
     * 
     */
    TRANSLATION,
    
    /**
     * 
     */
    REFERENCE
}
