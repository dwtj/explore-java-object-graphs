package me.dwtj.objectgraph;

import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests the `Explorer` class (with a default `Visitor` and `Navigator`) to make sure that
 * exploration terminates when expected.
 */
public class TestExplorerTermination
{
    Explorer default_explorer;

    @Before
    public void setUp() {
        default_explorer = new Explorer();
    }

    @Test
    public void nullTest()
    {
        final int EXPECTED_COUNT = 0;
        Object nullObj = null;
        default_explorer.explore(nullObj);
        TestVisitCount.assertVisitCount(default_explorer, nullObj, EXPECTED_COUNT);
    }

    @Test
    public void nullFieldTest()
    {
        List<String> listWithNullHeadField = new List<String>(null);
        default_explorer.explore(listWithNullHeadField);
        TestVisitCount.assertVisitCount(default_explorer, listWithNullHeadField, 1);
    }

    @Test
    public void boxedPrimitiveTest()
    {
        final int EXPECTED_COUNT = 1;
        Integer boxed_int = 42;
        default_explorer.explore(boxed_int);
        TestVisitCount.assertVisitCount(default_explorer, boxed_int, EXPECTED_COUNT);
    }
}
