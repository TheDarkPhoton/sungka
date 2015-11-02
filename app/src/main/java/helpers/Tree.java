package helpers;

import java.util.ArrayList;

public class Tree<T> {
    private Node<T> _root;
    private ArrayList<Node<T>> _children = new ArrayList<>();

    public Tree(T root){
        _root = new Node<>(root);
    }

    public Node<T> getRoot(){
        return _root;
    }
}
