package me.dwtj;

import com.googlecode.behaim.explorer.Explorer;

public class Main
{
    public static final int RECURSION_DEPTH = 1000;

    public static void main(String[] args)
    {
        System.out.println(">>>>>> Starting Main");

        String[] xs_arr = {"foo", "bar", "baz"};
        List<String> xs_list = List.<String>cat(xs_arr);
        System.out.println(">>>>>> xs_list: " + xs_list);
        Explorer explorer = new Explorer(new ObjectGraphLogger(), RECURSION_DEPTH);
        explorer.explore(xs_list);
    }
}
