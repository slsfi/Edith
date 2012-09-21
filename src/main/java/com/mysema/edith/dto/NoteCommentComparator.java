/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.io.Serializable;
import java.util.Comparator;

import com.mysema.edith.domain.NoteComment;

public final class NoteCommentComparator implements Comparator<NoteComment>, Serializable {

    private static final long serialVersionUID = 4759673633350279296L;

    public static final NoteCommentComparator ASC = new NoteCommentComparator();

    @Override
    public int compare(NoteComment o1, NoteComment o2) {
        if (o1.getCreatedAt().equals(o2.getCreatedAt())) {
            return 0;
        }
        return o1.getCreatedAt().isBefore(o2.getCreatedAt()) ? -1 : 1;
    }

}
