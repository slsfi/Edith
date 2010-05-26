package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public enum NoteFormat {
    WORD_EXPLANATION, LITERARY, HISTORICAL, DICTUM
}
