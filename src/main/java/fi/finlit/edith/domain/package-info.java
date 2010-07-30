/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
@MappedClasses( { Document.class, Identifiable.class, Note.class, DocumentNote.class,
        NoteStatus.class, Term.class, TermWithNotes.class, Profile.class, User.class,
        UserInfo.class, NoteType.class, NoteFormat.class, Person.class, Place.class,
        NameForm.class, Interval.class, NoteComment.class, Paragraph.class, ParagraphElement.class,
        StringElement.class, LinkElement.class })
@QuerydslConfig(entityAccessors = true)
package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.MappedClasses;
import com.mysema.query.annotations.QuerydslConfig;

