package me.dwtj;

import java.util.Collection;
import java.util.HashSet;

/**
 * Traverses a Java object graph and collects all of the references to all of the objects found.
 */

public class ReferenceCollector
{
    public final HashSet<Object> collected = new HashSet<Object>();

    public Collection<Object> collected() {
        return collected;
    }
}
