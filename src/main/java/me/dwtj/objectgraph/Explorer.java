package me.dwtj.objectgraph;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    /**
     * Greedily navigates an `Explorer` towards as many objects as possible (with a few exceptions).
     * It generally navigates towards all reference fields of a given object, even its private ones.
     * Examples of fields towards which this class does not navigate:
     *
     *  - Fields with primitive types.
     *  - Fields containing `null` values.
     *  - Fields of `String` objects.
     *  - Fields of a primitive-boxing class (e.g. `Integer`).
     */
    public static class GreedyNavigator implements Navigator
    {
        public Iterable<Object> navigate(Object obj)
        {
            /**
             * Note that a child object is included in `rv` if and only if three conditions are met.
             */
            List<Object> rv = new ArrayList<Object>();

            if (isNavigableObject(obj) == false) {
                return rv;
            }

            for (Field field : obj.getClass().getDeclaredFields())
            {
                if (isNavigableField(field))
                {
                    try {
                        field.setAccessible(true);
                        Object fieldValue = field.get(obj);
                        if (isNavigableFieldValue(fieldValue)) {
                            rv.add(fieldValue);
                        }
                    }
                    catch (IllegalAccessException ex) {
                        // TODO: Log and/or handle this.
                        ex.printStackTrace();
                    }
                    catch (IllegalArgumentException ex) {
                        // TODO: Log and/or handle this.
                        ex.printStackTrace();
                    }
                }
            }
            return rv;
        }

        /**
         * Returns true if navigation out from this object should be attempted.
         */
        protected static boolean isNavigableObject(Object obj)
        {
            return (obj == null
                 || obj instanceof String
                 || isBoxedPrimitiveInstance(obj)) ? false : true;
        }

        /**
         * Returns true if navigation to the object at the given field is expected to be navigable
         * based on the field's type.
         */
        protected static boolean isNavigableField(Field f)
        {
            return isPrimitiveField(f) ? false : true;
        }

        /**
         * Returns true if navigation towards this field should be attempted.
         */
        protected static boolean isNavigableFieldValue(Object obj)
        {
            return (obj == null) ? false : true;
        }

        /**
         * Returns `true` if and only if the given `obj` is an instance of one of the primitive-
         * boxing types, for example, an `Integer`.
         */
        private static boolean isBoxedPrimitiveInstance(Object obj)
        {
            return (obj instanceof Boolean
                 || obj instanceof Byte
                 || obj instanceof Character
                 || obj instanceof Double
                 || obj instanceof Enum
                 || obj instanceof Float
                 || obj instanceof Integer
                 || obj instanceof Long
                 || obj instanceof Short) ? true : false;
        }

        private static boolean isPrimitiveField(Field f)
        {
            return f.getType().isPrimitive();
        }
    }
}
