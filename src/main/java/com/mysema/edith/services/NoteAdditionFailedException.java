/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import com.mysema.edith.dto.SelectedText;

@SuppressWarnings("serial")
public class NoteAdditionFailedException extends Exception {

    private final SelectedText selectedText;

    private final String localId;

    public NoteAdditionFailedException(SelectedText selectedText, String localId,
            boolean startMatched, boolean endMatched) {
        super(createDescription(localId, startMatched, endMatched));
        this.selectedText = selectedText;
        this.localId = localId;
    }

    private static String createDescription(String localId, boolean startMatched, boolean endMatched) {
        StringBuilder builder = new StringBuilder();
        builder.append("Failed to add selected text for note #").append(localId);
        if (!startMatched) {
            builder.append("; start was not matched");
        }
        if (!endMatched) {
            builder.append("; end was not matched");
        }
        return builder.toString();
    }

    public SelectedText getSelectedText() {
        return selectedText;
    }

    public String getLocalId() {
        return localId;
    }

}
