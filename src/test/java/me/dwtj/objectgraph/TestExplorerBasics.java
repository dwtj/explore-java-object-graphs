package me.dwtj.objectgraph;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import me.dwtj.objectgraph.util.BinaryTree;
import me.dwtj.objectgraph.util.List;
import me.dwtj.objectgraph.util.Loop;
import me.dwtj.objectgraph.util.SelfLoop;

/**
 * Tests the `Explorer` class (with a default `Visitor` and `Navigator`) to make sure that the
 * actual counted number of visited object is as expected.
 */
public class TestExplorerBasics
{
    Explorer default_explorer;

    String[] str_arr;
    List<String> str_list;

    BinaryTree<String> leaf_a;
    BinaryTree<String> leaf_b;
    BinaryTree<String> leaf_c;
    BinaryTree<String> leaf_d;

    BinaryTree<String> subtree_1;
    BinaryTree<String> subtree_2;
    BinaryTree<String> tree;

    BinaryTree<String> dag;

    @Before
    public void setUp()
    {
        default_explorer = new Explorer();

        str_arr = new String[3];
        str_arr[0] = "foo";
        str_arr[1] = "bar";
        str_arr[2] = "baz";
        str_list = List.<String>cat(str_arr);

        leaf_a = BinaryTree.<String>cons("leaf_a", null, null);
        leaf_b = BinaryTree.<String>cons("leaf_b", null, null);
        leaf_c = BinaryTree.<String>cons("leaf_c", null, null);
        leaf_d = BinaryTree.<String>cons("leaf_d", null, null);

        subtree_1 = BinaryTree.<String>cons("subtree_1", leaf_a, leaf_b);
        subtree_2 = BinaryTree.<String>cons("subtree_2", leaf_c, leaf_d);
        tree = BinaryTree.<String>cons("tree", subtree_1, subtree_2);

        dag = BinaryTree.<String>cons("dag", subtree_1, leaf_a);
    }

    @Test
    public void smallStringArray()
    {
        final int EXPECTED_COUNT = 4;
        default_explorer.explore(str_arr);
        assertVisitCount(default_explorer, str_arr, EXPECTED_COUNT);
    }

    @Test
    public void smallStringList()
    {
        final int EXPECTED_COUNT = 6;
        default_explorer.explore(str_list);
        assertVisitCount(default_explorer, str_list, EXPECTED_COUNT);
    }
    
    @Test
    public void stringArrayAndListIdempotency()
    {
        // Initially zero.
        assertVisitCount(default_explorer, null, 0);

        // Explore the array.
        default_explorer.explore(str_arr);
        assertVisitCount(default_explorer, str_arr, 4);

        // Explore a list which shares elements with the array.
        default_explorer.explore(str_list);
        assertVisitCount(default_explorer, str_list, 7);

        // Explore that list again, and see no change.
        default_explorer.explore(str_list);
        assertVisitCount(default_explorer, str_list, 7);
        
        // Explore the array again, and see no change.
        default_explorer.explore(str_arr);
        assertVisitCount(default_explorer, str_list, 7);
    }

    @Test
    public void smallStringTree()
    {
        final int EXPECTED_COUNT = 14;
        default_explorer.explore(tree);
        assertVisitCount(default_explorer, tree, EXPECTED_COUNT);
    }

    @Test
    public void smallStringDAG()
    {
        final int EXPECTED_COUNT = 8;
        default_explorer.explore(dag);
        assertVisitCount(default_explorer, dag, EXPECTED_COUNT);
    }

    @Test
    public void selfLoop()
    {
        final int EXPECTED_COUNT = 1;
        SelfLoop self_loop = new SelfLoop();
        default_explorer.explore(self_loop);
        assertVisitCount(default_explorer, self_loop, EXPECTED_COUNT);
    }

    @Test
    public void smallLoop()
    {
        final int EXPECTED_COUNT = 3;
        final int NUM_LINKS = 3;
        Loop loop = Loop.cons(NUM_LINKS);
        default_explorer.explore(loop);
        assertVisitCount(default_explorer, loop, EXPECTED_COUNT);
    }

    public static void assertVisitCount(Explorer explorer, Object obj, int expected_count)
    {
        int actual_count = explorer.visited.size();
        String msg = "Exploring \"" + obj + "\" and counting visited objects...";
        assertEquals(msg, expected_count, actual_count);
    }
}
