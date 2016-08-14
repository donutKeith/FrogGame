package com.mygdx.game;

/**
 * Created by Keith on 8/22/2015.
 */

public class MyObjectList<T> {
    protected MyNode<T> head;
    int size;

    public MyObjectList(){
        head = null;
        size = 0;
    }

    public MyObjectList(T obj){
        size = 0;
        Add(obj); // Add will add to the size
    }

    public MyNode<T> GetHead(){
        return this.head;
    }

    public boolean Add(T obj){
        // Add a node with obj = parameter obj  to this list
        // Return true if we successfully added the node, false otherwise
        boolean objAlreadyAdded = false;
        MyNode<T> curNode = head;
        MyNode<T> newNode = new MyNode<T>(obj, size);

        if(head == null){ // Add this node to the head if the head is empty or the head is teh same as the object we are adding
            head = newNode;
        }
        else {
            if(head.GetObject() == newNode.GetObject()) { // Check if this object matches the first object in the list
                objAlreadyAdded = true;
            }
            else {
                while (curNode.GetNext() != null && !objAlreadyAdded) {    // Loop until we hit the
                    if (curNode.GetNext().GetObject() != obj) {    // Check if this node contains the obj we are adding, this way we do not add the same obj twice
                        curNode = curNode.GetNext();
                    } else {                            // Only move on if this node did not already have the obj we are adding
                        objAlreadyAdded = true;
                    }
                }
                if(!objAlreadyAdded) {
                    curNode.AssignToNext(newNode);
                }
            }
        }

        if(!objAlreadyAdded){
            size++;
            return true;		// We added something to the list so return true
        }
        else{
            return false;		// We did not add anything so return false
        }
    }

    public boolean Remove(T obj){
        // Removed the node with the parameter obj as it's obj variable.  Does not delete anything if the node is not found
        // Returns true if something was deleted and false if nothing was deleted
        boolean removed = false;
        MyNode<T> curNode = head;
        MyNode<T> prevNode = head;
        MyNode<T> nextNode;

        if(curNode.GetObject() == obj){// Check if the head is the object that needs to be removed
            nextNode = head.GetNext();
            head = nextNode;
            removed = true;
        }
        else{
            while(curNode.GetObject() != obj && curNode != null){
                prevNode = curNode;
                curNode = curNode.GetNext();
            }

            if(curNode != null) {
                if (curNode.GetObject() == obj) { // Make sure we found the node we want to delete and did not just hit the end of the list
                    nextNode = curNode.GetNext();
                    prevNode.AssignToNext(nextNode);
                    curNode = null;
                    removed = true;
                }
                else {
                    removed = false;    // We did not remove anything so return false
                }
            }
            else{ // There was no node that matched the one we wanted removed so we hit the end of the list
                removed = false;
            }
        }

        if(removed == true){
            this.size--;
        }
        return removed;
    }

    public T Get(T obj){
        // Looks through the list for the node that has obj and returns that obj
        //  if the obj cannot be found it returns null
        MyNode<T> curNode = head;
        while(curNode.GetObject() != obj && curNode != null){
            curNode = curNode.GetNext();
        }

        if(curNode.GetObject() == obj){
            return curNode.GetObject();
        }
        else{
            return null;
        }
    }

    public boolean Set (T lookFor, T replaceWith){
        boolean ret = false;
        MyNode<T> curNode = head;
        while(curNode.GetObject() != lookFor && curNode != null && !ret){
            if(lookFor == curNode.GetObject()){
                curNode.Set(replaceWith);
                ret = true;
            }
            curNode = curNode.GetNext();
        }
        return ret;
    }

    public int GetSize(){
        return size;
    }

    public void printListIDs(MyObjectList mol){
        if(size > 0) {
            for (MyNode counter = mol.head; counter != null; counter = counter.GetNext()) {
                System.out.println(counter.getID());
            }
        }
        else{
            System.out.println("There is nothing to print");
        }
    }
}


