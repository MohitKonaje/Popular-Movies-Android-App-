package com.example.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by mk3s on 26-Oct-17.
 */

public class AsyncTaskForMovieDataCollection extends AsyncTask<URL,Integer,MovieDetails[]> {
    Context context;
  private InterfaceListener.onTaskCompleteListener<MovieDetails[]>  listener;

    //fetch task
    public  AsyncTaskForMovieDataCollection(Context ctx, InterfaceListener.onTaskCompleteListener<MovieDetails[]> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }


    @Override
    protected MovieDetails[] doInBackground(URL... urls) {
        //get results url
        URL searchUrl = urls[0];
        //initialise results string
        String httpResponse;

        //initialise movie details data object for parsed_results
        MovieDetails[] parsedResults= null;

        try {
            //get results of movies
            httpResponse = NetworkUtils.get_movie_details(searchUrl);
            //filter and store results of movies
            parsedResults= MovieJsonUtils.get_values(httpResponse);
        }catch (IOException e){
            e.printStackTrace();
        }

        return parsedResults;
    }

    @Override
    protected void onPostExecute(MovieDetails[] parsedResults) {
        //end Async task by sending movie data to method implementing the interface
       listener.onTaskComplete(parsedResults);
        }
    }


