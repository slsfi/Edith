/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mysema.edith.dto.SelectedText;


public class NoteAdditionFailedExceptionTest {

    @Test
    public void Both_Matched() throws Exception {
        assertEquals("Failed to add selected text for note #id666",
                new NoteAdditionFailedException(new SelectedText(), "id666", true, true)
                        .getMessage());
    }

    @Test
    public void Start_Matched() throws Exception {
        assertEquals("Failed to add selected text for note #id666; end was not matched",
                new NoteAdditionFailedException(new SelectedText(), "id666", true, false)
                        .getMessage());
    }

    @Test
    public void End_Matched() throws Exception {
        assertEquals("Failed to add selected text for note #id666; start was not matched",
                new NoteAdditionFailedException(new SelectedText(), "id666", false, true)
                        .getMessage());
    }

    @Test
    public void Neither_Matched() throws Exception {
        assertEquals(
                "Failed to add selected text for note #id666; start was not matched; end was not matched",
                new NoteAdditionFailedException(new SelectedText(), "id666", false, false)
                        .getMessage());
    }

    @Test
    public void Get_Selected_Text() {
        SelectedText selectedText = new SelectedText();
        assertEquals(selectedText, new NoteAdditionFailedException(selectedText, "id666", false,
                false).getSelectedText());
    }

    @Test
    public void Get_Local_Id() {
        String localId = "id666";
        assertEquals(localId, new NoteAdditionFailedException(new SelectedText(), localId, false,
                false).getLocalId());
    }

}
