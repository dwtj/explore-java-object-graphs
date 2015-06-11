package me.dwtj.objectgraph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * It generally navigates towards all references held by a given object, even if those
     * references are held in private fields.
     *
     * Examples of fields towards which this class does not navigate:
     *
     *  - Fields with primitive types.
     *  - Fields containing `null` values.
     *  - Fields of `String` objects.
     *  - Fields of a primitive-boxing class (e.g. `Integer`).
     * 
     * Arrays are treated as a special case. If a given `Object` is an array of reference types,
     * then this will navigate towards every non-`null` element in the array. If it is an array of
     * primitives, then there will be no outgoing navigation.
     */
    public static class GreedyNavigator implements Navigator
    {
        public static final Predicate<Object> DEFAULT_NAV_FROM = (obj -> !(obj instanceof String));
        public static final Predicate<Class<?>> DEFAULT_NAV_TO = (clazz -> true);
        
        private final Predicate<Object> nav_from;
        private final Predicate<Class<?>> nav_to;
        

        /**
         * By default, this navigator will `nav_from` any object that isn't a `String`.
         * By default, this navigator will `nav_to` is any type.
         */
        public GreedyNavigator()
        {
            nav_from = DEFAULT_NAV_FROM;
            nav_to = DEFAULT_NAV_TO;
        }
        

        /**
         * @param nav_from Whether any navigation *from* a given object instance should be performed.
         * @param nav_to   Whether any navigation *to* a given class type should be performed.
         */
        public GreedyNavigator(Predicate<Object> nav_from, Predicate<Class<?>> nav_to)
        {
            this.nav_from = nav_from;
            this.nav_to = nav_to;
        }
        

        public Iterable<Object> navigate(Object obj)
        {
            if (isNavigableArray(obj))
            {
                return navigateFromArray((Object[]) obj);
            }
            else if (isNavigableObject(obj))
            {
                return navigateFromObject(obj);
            }
            else
            {
                return new ArrayList<Object>(0);
            }
        }


        private Iterable<Object> navigateFromArray(Object[] arr)
        {
            return Arrays.stream(arr).filter(obj -> isNavigableValue(obj))
                                     .collect(Collectors.toList());
        }


        private Iterable<Object> navigateFromObject(Object obj)
        {
            List<Object> navigable = new ArrayList<Object>();

            for (Field field : obj.getClass().getDeclaredFields())
            {
                if (isNavigableField(field))
                {
                    try {
                        field.setAccessible(true);
                        Object fieldValue = field.get(obj);
                        if (isNavigableValue(fieldValue)) {
                            navigable.add(fieldValue);
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

            return navigable;
        }


        /**
         * Returns true if `obj` is a non-array object from which outgoing navigation might be
         * attempted.
         */
        protected boolean isNavigableObject(Object obj)
        {
            return (obj != null
                 && obj.getClass().isArray() == false
                 && isBoxedPrimitiveInstance(obj) == false
                 && nav_from.test(obj));
        }


        /**
         * Returns true if `obj` is an array of reference types for which `nav_to` is true.
         */
        protected boolean isNavigableArray(Object obj)
        {
            Class<?> clazz = obj.getClass();

            return clazz.isArray()
                && obj instanceof Object[]
                && nav_to.test(clazz.getComponentType());
        }


        /**
         * Returns true if navigation to the object at the given field is expected to be navigable
         * based on the field's type.
         */
        protected boolean isNavigableField(Field f)
        {
            return isPrimitiveField(f) == false && nav_to.test(f.getType());
        }


        /**
         * Returns true if navigation towards this value should be attempted.
         */
        protected boolean isNavigableValue(Object obj)
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
