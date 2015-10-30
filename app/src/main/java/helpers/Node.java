package helpers;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class Node<T> {
    private Node<T> _parent;
    private ArrayList<Node<T>> _children = new ArrayList<>();
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

    public int getChildrenCount(){
        return _children.size();
    }
    public Node<T> getChild(int index){
        return _children.get(index);
    }
    public void removeChild(int index){
        _children.remove(index);
    }
    public void addChild(Node<T> child){
        _children.add(child);
    }

//    public void sortChildren(){
//        Collections.sort(_children);
//    }

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
}
