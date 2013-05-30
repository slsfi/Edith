/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;

import com.mysema.edith.domain.NoteComment;

public final class NoteCommentComparator implements Comparator<NoteComment>, Serializable {

    private static final long serialVersionUID = 4759673633350279296L;

    public static NoteComment getLatest(Collection<NoteComment> comments) {
        if (!comments.isEmpty()) {
            if (comments.size() == 1) {
                return comments.iterator().next();
            } else {
                NoteComment latest = null;
                for (NoteComment comment : comments) {
                    if (latest == null || latest.getCreatedAt().isBefore(comment.getCreatedAt())) {
                        latest = comment;
                    }
                }
                return latest;
            }
        } else {
            return null;
        }
    }

    public static final NoteCommentComparator ASC = new NoteCommentComparator();

    @Override
    public int compare(NoteComment o1, NoteComment o2) {
        return o1.getCreatedAt().compareTo(o2.getCreatedAt());
    }

}
