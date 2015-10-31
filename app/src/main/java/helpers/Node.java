package helpers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private Node<T> _parent;
    private List<Node<T>> _children = new ArrayList<>();
    private T _element;

    public Node(T element){
        _element = element;
    }

    public Node<T> getParent(){
        return _parent;
    }
    public void setParent(Node<T> parent){
        _parent = parent;
    }

    public List<Node<T>> getChildren(){
        return _children;
    }
    public int getChildrenCount(){
        return _children.size();
    }

//    public Node<T> keepFirstChild(){
//        _children = _children.subList(0, 1);
//        return _children.get(0);
//    }

    public Node<T> getFirstLeaf(){
        return _children.get(0);
    }
    public Node<T> getFirstChild(){
        return _children.get(0);
    }
    public Node<T> getChild(int index){
        return _children.get(index);
    }
    public void removeChild(int index){
        _children.get(index).setParent(null);
        _children.remove(index);
    }
    public void addChild(Node<T> child){
        child.setParent(this);
        _children.add(child);
    }

    public T getElement(){
        return  _element;
    }

    public void setElement(T element){
        _element = element;
    }

    public void printDebug(){
        for (int i = 0; i < _children.size(); i++) {
            Log.i("Node", _children.get(i).getElement().toString());
        }
    }

    @Override
    public String toString() {
        return _element.toString();
    }
}
