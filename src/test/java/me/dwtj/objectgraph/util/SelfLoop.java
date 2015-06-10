package me.dwtj.objectgraph.util;

public class SelfLoop
{
    @SuppressWarnings("unused")
    private SelfLoop pointer;

    public SelfLoop() {
        pointer = this;
    }
}
