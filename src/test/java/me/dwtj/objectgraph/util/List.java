package me.dwtj.objectgraph.util;

public class List<T>
{
    private T head;
    private List<T> tail;

    public List(T head) {
        this.head = head;
        this.tail = List.<T>nil();
    }

    public List(T head, List<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public T head() {
        return head;
    }

    public List<T> tail() {
        return tail;
    }

    public static <T> List<T> nil() {
        return null;
    }

    public static <T> List<T> cat(T[] values)
    {
        List<T> xs = List.<T>nil();
        for (int idx = values.length - 1; idx >= 0; idx--) {
            xs = new List<T>(values[idx], xs);
        }
        return xs;
    }

    public String toString() {
        String head_str = (head == null ? "<null>" : head.toString());
        if (tail == List.<T>nil()) {
            return head_str;
        } else {
            return head_str + " " + tail.toString();
        }
    }
}
