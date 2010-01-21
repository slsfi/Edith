/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
@MappedClasses( { 
    Document.class, 
    Folder.class,
    Identifiable.class,
    Location.class,
    Note.class, 
    NoteComment.class,
    NoteRevision.class, 
    NoteStatus.class,
    Person.class,
    Term.class,
    TermWithNotes.class,
    Profile.class,    
    Tag.class, 
    User.class,
    UserInfo.class})
package fi.finlit.edith.domain;

import com.mysema.query.annotations.QuerydslConfig;
import com.mysema.rdfbean.annotations.MappedClasses;


