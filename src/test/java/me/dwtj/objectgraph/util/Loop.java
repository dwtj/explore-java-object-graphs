package me.dwtj.objectgraph.util;

public class Loop
{
    @SuppressWarnings("unused")
    private Loop ptr;

    public static Loop cons(int num_links)
    {
        assert num_links > 0;

        // Initialize each of the links. Set each link to point at the next except for the last one.
        // Set the last link to point to the first link.
        Loop[] links = new Loop[num_links];
        for (int idx = 0; idx < num_links; idx++) {
            links[idx] = new Loop();
        }
        for (int idx = 0; idx < num_links - 1; idx++)
        {
            links[idx].ptr = links[idx+1];
        }
        links[num_links-1].ptr = links[0];
        return links[0];
    }
}
