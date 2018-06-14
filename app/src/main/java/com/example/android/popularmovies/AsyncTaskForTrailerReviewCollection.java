package com.example.android.popularmovies;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;


public class AsyncTaskForTrailerReviewCollection extends AsyncTaskLoader<MovieDetails> {
    MovieDetails mMovie;

    public AsyncTaskForTrailerReviewCollection(Context context,MovieDetails mMovie) {
        super(context);
        this.mMovie=mMovie;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public MovieDetails loadInBackground() {

        MovieDetails selectedMovie=mMovie;
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

            //format trailer json data into movie data format
            selectedMovie = MovieJsonUtils.get_reviews_values(reviewJson, selectedMovie);
        }catch (java.io.IOException e){e.printStackTrace();
        }
        return selectedMovie;
    }

}