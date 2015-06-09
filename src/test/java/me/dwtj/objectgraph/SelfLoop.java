package me.dwtj.objectgraph;

public class SelfLoop
{
    private SelfLoop pointer;
    public SelfLoop() {
        pointer = this;
    }
}
