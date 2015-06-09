package me.dwtj.objectgraph;

public interface Navigator
{
    /**
     * Inspects the given `obj` and returns an iterable of object to which the explorer should
     * or might next navigate.
     */
    Iterable<Object> navigate(Object obj);
}
