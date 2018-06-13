package com.example.android.popularmovies;


public class InterfaceListener {

    public interface onTaskCompleteListener<T>{
         void onTaskComplete(T result);
    }
}
