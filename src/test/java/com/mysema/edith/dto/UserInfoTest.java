package com.mysema.edith.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UserInfoTest {

    @Test
    public void Equals_Is_True_When_Same_Object() {
        UserInfo u = new UserInfo();
        assertEquals(u, u);
    }

    @Test
    public void Equals_Is_False_When_Other_Is_Null() {
        UserInfo u = new UserInfo();
        assertFalse(u.equals(null));
    }

    @Test
    public void Equals_Is_False_When_Other_Is_Different_Type() {
        UserInfo u = new UserInfo();
        assertFalse(u.equals("foo"));
    }

    @Test
    public void Equals_Is_False_When_Other_Username_Is_Null() {
        UserInfo u = new UserInfo("boo");
        assertFalse(u.equals(new UserInfo()));
    }

    @Test
    public void Equals_Is_False_When_Other_Username_Is_Not_Null() {
        UserInfo u = new UserInfo();
        assertFalse(u.equals(new UserInfo("boo")));
    }

    @Test
    public void Equals_Is_True_When_Both_Have_The_Same_Username() {
        UserInfo u = new UserInfo("boo");
        assertEquals(u, new UserInfo("boo"));
    }
}
