package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public enum NoteType {
    WORD_EXPLANATION, LITERARY, HISTORICAL, DICTUM, CRITIQUE
}
