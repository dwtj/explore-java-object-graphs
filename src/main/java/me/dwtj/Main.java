package me.dwtj;

import me.dwtj.objectgraph.Explorer;
import me.dwtj.util.Log;

public class Main
{
    public static final int RECURSION_DEPTH = 1000;

    public static void main(String[] args)
    {
        System.out.println(">>>>>> Starting Main");

        String[] xs_arr = {"foo", "bar", "baz"};
        List<String> xs_list = List.<String>cat(xs_arr);
        Log.debug("xs_list: " + xs_list);

        Explorer explorer = new Explorer();
        explorer.explore(xs_list);
        Log.debug("" + explorer.visited.size());
    }
}
