package com.mygdx.game;

/**
 * Created by Keith on 8/22/2015.
 */
public class MyNode<T> {

    private MyNode<T> next;
    private T obj;
    private int id;

    public MyNode(T obj){
        this.next = null;
        this.obj = obj;
        this.id = -1;
    }

    public MyNode(T obj, int id){
        this.next = null;
        this.obj = obj;
        this.id = id;
    }

    public void Set(T obj){
        this.obj = obj;
    }

    public int getID(){return this.id;}

    public MyNode<T> GetNext(){
        return next;
    }

    public void AssignToNext(MyNode<T> n){
        this.next = n;
    }

    public T GetObject(){
        return obj;
    }

    public void DrawNode(){}

}


