package com.example.android.popularmovies;

/**
 * Created by mk3s on 26-Oct-17.
 */

public class InterfaceListener {

    public interface onTaskCompleteListener<T>{
        public void onTaskComplete(T result);
    }
}
