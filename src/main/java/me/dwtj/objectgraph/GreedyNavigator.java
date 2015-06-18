package me.dwtj.objectgraph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class GreedyNavigator implements Navigator
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

        List<Field> fields = Stream.concat(Arrays.stream(obj.getClass().getFields()),
                                           Arrays.stream(obj.getClass().getDeclaredFields()))
                                   .collect(Collectors.toList());
        for (Field field : fields)
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
