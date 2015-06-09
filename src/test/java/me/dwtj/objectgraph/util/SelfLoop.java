package me.dwtj.objectgraph.util;

public class SelfLoop
{
    private SelfLoop pointer;
    public SelfLoop() {
        pointer = this;
    }
}
