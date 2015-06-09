package me.dwtj.objectgraph.util;

public class BinaryTree<T>
{
    public final T data;
    public final BinaryTree<T> left;
    public final BinaryTree<T> right;

    private BinaryTree(T data, BinaryTree<T> left, BinaryTree<T> right)
    {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public static <T> BinaryTree<T> cons(T data, BinaryTree<T> left, BinaryTree<T> right)
    {
        return new BinaryTree<T>(data, left, right);
    }
}
