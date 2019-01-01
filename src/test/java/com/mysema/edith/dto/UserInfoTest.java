/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UserInfoTest {

    @Test
    public void Equals_Is_True_When_Same_Object() {
        UserTO u = new UserTO();
        assertEquals(u, u);
    }

    @Test
    public void Equals_Is_False_When_Other_Is_Null() {
        UserTO u = new UserTO();
        assertFalse(u.equals(null));
    }

    @Test
    public void Equals_Is_False_When_Other_Is_Different_Type() {
        UserTO u = new UserTO();
        assertFalse(u.equals("foo"));
    }

    @Test
    public void Equals_Is_False_When_Other_Username_Is_Null() {
        UserTO u = new UserTO("boo");
        assertFalse(u.equals(new UserTO()));
    }

    @Test
    public void Equals_Is_False_When_Other_Username_Is_Not_Null() {
        UserTO u = new UserTO();
        assertFalse(u.equals(new UserTO("boo")));
    }

    @Test
    public void Equals_Is_True_When_Both_Have_The_Same_Username() {
        UserTO u = new UserTO("boo");
        assertEquals(u, new UserTO("boo"));
    }
}
