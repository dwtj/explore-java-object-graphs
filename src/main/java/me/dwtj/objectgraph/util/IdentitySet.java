package me.dwtj.objectgraph.util;

import java.util.Collection;
import java.util.IdentityHashMap;

/**
 * A set for holding objects based on their identities.
 */
public class IdentitySet
{
    public final IdentityHashMap<Object, Void> identities = new IdentityHashMap<Object, Void>();

    public void add(Object obj) {
        identities.put(obj, null);
    }

    public void addAll(Iterable<Object> objs) {
        for (Object obj : objs) {
            identities.put(obj, null);
        }
    }

    public boolean contains(Object obj) {
        return identities.containsKey(obj);
    }

    public int size() {
        return identities.size();
    }
}
