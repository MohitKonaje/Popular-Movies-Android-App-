package com.example.android.popularmovies;


import android.os.AsyncTask;

import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by mk3s on 27-Feb-18.
 */

public class AsycTaskForTrailerReviewCollection extends AsyncTask<MovieDetails,Integer,MovieDetails>{
    String [] trailerUrls;
    @Override
    protected MovieDetails doInBackground(MovieDetails... movies) {
        MovieDetails selectedMovie=movies[0];
        String movieId=selectedMovie.movieId;
        try {
            //get trailer and review url for the selected movie
            URL[] trailers_review_json_url = NetworkUtils.trailer_review_url_builder(movieId);

            //get Json Data
            String trailerJson = NetworkUtils.get_movie_details(trailers_review_json_url[0]);
            String reviewJson = NetworkUtils.get_movie_details(trailers_review_json_url[1]);

            //filter trailer source data
            String[] trailer_source_url = MovieJsonUtils.get_trailer_values(trailerJson);
            //convert trailer sources into youtube urls
            selectedMovie = NetworkUtils.youtube_trailer_url_builder(trailer_source_url, selectedMovie);


            //format trailer json data into moviedata format
            selectedMovie = MovieJsonUtils.get_reviews_values(reviewJson, selectedMovie);
        }catch (java.io.IOException e){e.printStackTrace();
        }
        return selectedMovie;
    }


}