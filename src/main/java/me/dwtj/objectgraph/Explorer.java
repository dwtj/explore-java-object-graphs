package me.dwtj.objectgraph;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.dwtj.util.IdentitySet;
import me.dwtj.util.Log;

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
 * If no `Visitor` is provided at construction, then a default visitor is used. The default visitor
 * takes no action on an object upon visitation.  Similarly, if no `Navigator` is provided at
 * construction, a default navigator is used. The default navigator points the explorer towards all
 * fields which are reference types (e.g. arrays and objects).
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
        navigator = new DefaultNavigator();
    }

    public Explorer(Navigator n) {
        visitor = new DefaultVisitor();
        navigator = n;
    }

    public Explorer() {
        visitor = new DefaultVisitor();
        navigator = new DefaultNavigator();
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
     * Does nothing when it visits an object.
     */
    public static class DefaultVisitor implements Visitor {
        public void visit(Object obj) { /* no-op */ }
    }


    /**
     * Greedily navigates an explorer towards as many objects as possible.
     */
    public static class DefaultNavigator implements Navigator
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
                        Log.debug("Explorer.DefaultNavigator.navigate(): IllegalAccessException");
                    }
                    catch (IllegalArgumentException ex) {
                        throw new AssertionError();
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
         * Returns true 
         */
        protected static boolean isNavigableField(Field f)
        {
            // TODO: Everything!
            return true;
        }

        /**
         * Returns true if navigation towards this field should be attempted.
         */
        protected static boolean isNavigableFieldValue(Object obj)
        {
            return (obj == null) ? false : true;
        }

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
    }
}
