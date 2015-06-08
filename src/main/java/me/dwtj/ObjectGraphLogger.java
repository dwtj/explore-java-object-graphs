package me.dwtj;

import java.lang.reflect.Field;

import java.util.Collection;
import java.util.HashSet;

import com.googlecode.behaim.explorer.VisitationResult;
import com.googlecode.behaim.explorer.Visitor;


/**
 * Taverses an arbitrary Java object graph and prints out some information.
 */
public class ObjectGraphLogger implements Visitor
{
	public VisitationResult visit(Object object, Field field)
    {
        System.out.println(">>>>>> Object: " + object);
        System.out.println(">>>>>> Field: " + field);
        return null;
    }
}

