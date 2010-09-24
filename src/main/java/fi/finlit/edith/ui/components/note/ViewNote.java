/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components.note;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Interval;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;

public class ViewNote {
    @Property
    @Parameter(required = true)
    private DocumentNote documentNote;

    public Note getNote() {
        return documentNote.getNote();
    }

    public boolean isNormalNote() {
        return documentNote.getNote().getFormat() == NoteFormat.NOTE;
    }

    public boolean isPersonNote() {
        return documentNote.getNote().getFormat() == NoteFormat.PERSON;
    }

    public Person getPerson() {
        return documentNote.getNote().getPerson();
    }

    public boolean isPlaceNote() {
        return documentNote.getNote().getFormat() == NoteFormat.PLACE;
    }

    public Place getPlace() {
        return documentNote.getNote().getPlace();
    }

    public String getLifeDuration() {
        Interval timeOfBirth = getPerson().getTimeOfBirth();
        Interval timeOfDeath = getPerson().getTimeOfDeath();
        if (timeOfBirth != null || timeOfDeath != null) {
            StringBuilder builder = new StringBuilder();
            if (timeOfBirth != null) {
                builder.append(timeOfBirth.asString());
            }
            builder.append("\u2013");
            if (timeOfDeath != null) {
                builder.append(timeOfDeath.asString());
            }
            builder.append(".");
            return builder.toString();
        }
        return null;
    }
}
