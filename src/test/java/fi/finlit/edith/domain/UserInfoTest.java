package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UserInfoTest {
    private UserInfo userInfo;
    private static final String username = "foobar";

    @Before
    public void before() {
        userInfo = new UserInfo(username);
    }

    @Test
    public void Equals_When_Same_Object() {
        assertTrue(userInfo.equals(userInfo));
    }

    @Test
    public void Does_Not_Equal_When_Other_Is_Null() {
        assertFalse(userInfo.equals(null));
    }

    @Test
    public void Does_Not_Equal_When_Different_Class() {
        assertFalse(userInfo.equals("foobar"));
    }

    @Test
    public void Does_Not_Equal_When_This_Username_Is_Null_And_Other_Is_Not() {
        userInfo = new UserInfo();
        assertFalse(userInfo.equals(new UserInfo("something")));
    }

    @Test
    public void Does_Not_Equal_If_Usernames_Are_Not_Equal() {
        assertFalse(userInfo.equals(new UserInfo("something")));
    }

    @Test
    public void Equals_When_Username_Equals() {
        assertTrue(userInfo.equals(new UserInfo(username)));
    }

    @Test
    public void Hash_Code_When_Username_Is_Set() {
        assertEquals(31 + username.hashCode(), userInfo.hashCode());
    }

    @Test
    public void Hash_Code_When_Username_Is_Null() {
        assertEquals(31, new UserInfo().hashCode());
    }

}
