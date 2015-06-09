package me.dwtj.objectgraph;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests the `Explorer` class (with a default `Visitor` and `Navigator`) to make sure that the
 * actual counted number of visited object is as expected.
 */
public class TestExplorerBasics
{
    Explorer default_explorer;

    @Before
    public void setUp()
    {
        default_explorer = new Explorer();
    }

    @Test
    public void smallStringList()
    {
        final int EXPECTED_COUNT = 6;

        String[] xs_arr = {"foo", "bar", "baz"};
        List<String> xs_list = List.<String>cat(xs_arr);
        default_explorer.explore(xs_list);

        assertVisitCount(default_explorer, xs_list, EXPECTED_COUNT);
    }

    public static void assertVisitCount(Explorer explorer, Object obj, int expected_count)
    {
        int actual_count = explorer.visited.size();
        String msg = "Exploring \"" + obj + "\" and counting visited objects...";
        assertEquals(msg, expected_count, actual_count);
    }
}
