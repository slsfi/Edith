/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NameFormTest {

    @Test
    public void Is_Not_Valid() {
        NameForm form1 = new NameForm();
        NameForm form2 = new NameForm("  ", "");
        assertFalse(form1.isValid());
        assertFalse(form2.isValid());
    }

    @Test
    public void Is_Valid_With_First_Name_Only() {
        NameForm form = new NameForm();
        form.setFirst("foo");
        assertTrue(form.isValid());
    }

    @Test
    public void Is_Valid_With_Last_Name_Only() {
        NameForm form = new NameForm();
        form.setLast("foo");
        assertTrue(form.isValid());
    }
}
