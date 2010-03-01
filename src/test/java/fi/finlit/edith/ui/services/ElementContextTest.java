package fi.finlit.edith.ui.services;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ElementContextTest {

    @Test
    public void testPush() {
        ElementContext context = new ElementContext(3);
        context.push("baz");
        context.push("bar");
        context.push("barbazzz");
        context.push("foo");
        assertEquals("foo", context.getPath());
        context.push("foobar");
        assertEquals("foo-foobar", context.getPath());
    }

    @Test
    public void testPop() {
        ElementContext context = new ElementContext(3);
        context.push("baz");
        context.push("bar");
        context.push("barbazzz");
        context.push("foo");
        assertEquals("foo", context.getPath());
        context.pop();
        assertEquals(null, context.getPath());
        context.push("baz");
        context.push("bar");
        assertEquals("baz-bar", context.getPath());
        context.pop();
        assertEquals("baz", context.getPath());
        context.push("bar");
        assertEquals("baz-bar2", context.getPath());
    }

    @Test
    public void testGetPath() {
        ElementContext context = new ElementContext(0);
        context.push("baz");
        context.push("bar");
        context.push("barbazzz");
        context.push("foo");
        assertEquals("baz-bar-barbazzz-foo", context.getPath());
        context.push("foobar");
        assertEquals("baz-bar-barbazzz-foo-foobar", context.getPath());
    }

    @Test
    public void testClone() throws Exception {
        ElementContext context = new ElementContext(0);
        context.push("foo");
        context.push("bar");
        assertEquals("foo-bar", context.getPath());
        ElementContext clonedContext = (ElementContext) context.clone();
        assertEquals(context.getPath(), clonedContext.getPath());
        context.pop();
        context.push("bar"); // bar will be bar2 in context
        assertThat(context.getPath(), not(equalTo(clonedContext.getPath())));
    }

}
