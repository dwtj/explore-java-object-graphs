package me.dwtj.objectgraph;

import me.dwtj.objectgraph.util.IdentitySet;

/**
 * Explores an object graph using a `Visitor` and a `Navigator`.
 *
 * The `Visitor` is responsible for visiting objects in some user-defined way. For example, a
 * `Visitor` might modify a visited object or collect some information from it. The `Navigator` is
 * responsible for inspecting an object and proposing subsequent objects to visit. Note that
 * visitation happens *before* navigation.
 *
 * The `Explorer` itself is responsible for guaranteeing no object is visited twice, even if the
 * `Navigator` points the explorer to a particular object more than once.
 *
 * If no `Visitor` is provided at construction, then a `NoOpVisitor` is instantiated and used. If
 * no navigator is provided at construction, a `GreedyNavigator` is instantiated and used.
 *
 * **Warning:** The current implementation naively recursive, so it is susceptible to stack overflow
 * if the explorer attempts to recurse down a long enough path of unique object in the object graph.
 */
public class Explorer
{
    public final IdentitySet visited = new IdentitySet();
    
    private final Visitor visitor;
    private final Navigator navigator;

    public Explorer(Visitor v, Navigator n) {
        visitor = v;
        navigator = n;
    }

    public Explorer(Visitor v) {
        visitor = v;
        navigator = new GreedyNavigator();
    }

    public Explorer(Navigator n) {
        visitor = new NoOpVisitor();
        navigator = n;
    }

    public Explorer() {
        visitor = new NoOpVisitor();
        navigator = new GreedyNavigator();
    }

    public void explore(Object obj)
    {
        if (obj == null) {
            return;
        }

        visited.add(obj);
        visitor.visit(obj);
        for (Object n : navigator.navigate(obj))
        {
            // If `n` hasn't been explored yet, explore it now.
            if (visited.contains(n) == false) {
                explore(n);  
            }
        }
    }


    /**
     * Does takes no action when it visits an object.
     */
    public static class NoOpVisitor implements Visitor {
        public void visit(Object obj) { /* no-op */ }
    }
}
