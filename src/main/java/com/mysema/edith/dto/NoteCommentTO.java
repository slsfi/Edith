/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.dto;

import org.joda.time.DateTime;

public class NoteCommentTO {
    private DateTime createdAt;

    private String message;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
