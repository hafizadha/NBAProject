package com.example.NBAProject.TeamRoster;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;


//Generic Stack Class
public class MyStack <E extends PlayerInfo> implements Iterable<E>{
    private ArrayList<E> list = new ArrayList<>();


    //Add Object into the top of the stack
    public void push(E o){
        list.add(o);
    }

    //Remove the top value of the stack and return the removed value
    public E pop(){
        E o = list.get(size() - 1);
        list.remove(size()-1);
        return o;
    }

    //Check if the stack contains specific elements
    public boolean contains(E o){
        return list.contains(o);
    }

    //Get elements inside the list given the position
    public E get(int position){
        return list.get(position);
    }

    //Return the top value of the stack without removing it
    public E peek(){
        return list.get(size()-1);
    }

    //Return the size
    public int size(){return list.size();}

    //Return true if empty
    public boolean isEmpty(){
        return list.isEmpty();
    }

    //Clear the stack
    public void clear(){
        list.clear();
    }

    @Override
    public String toString(){
        return "Stack" + list.toString();
    }

    //Implementation of Iterable interface
    //This overridden method allows this MyStack class usable for foreach loops
    @NonNull
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    //This overridden method provides an easy way to apply a function to every item in the collection
    @Override
    public void forEach(@NonNull Consumer<? super E> action) {
        Iterable.super.forEach(action);
    }

    //This overridden allows parallel processing ( by splitting into parts that can be processed simultaneous)
    //Speeds up performance for large collection of Object
    @NonNull
    @Override
    public Spliterator<E> spliterator() {
        return Iterable.super.spliterator();
    }
}
