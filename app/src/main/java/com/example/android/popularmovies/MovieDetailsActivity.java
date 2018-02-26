package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {
    String movieId;
    String [] trailerUrls;
    MovieDetails movie ;
    TextView titleText, releaseDate, voteAverage, plotSynopsis;
    ImageView movie_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set activity layout
        setContentView(R.layout.activity_movie_details_activity);

        //initialise views
        titleText = (TextView)findViewById(R.id.movie_title);
        releaseDate = (TextView)findViewById(R.id.release_date);
        voteAverage = (TextView)findViewById(R.id.vote_average);
        plotSynopsis = (TextView)findViewById(R.id.plot_synopsis);
        movie_poster = (ImageView)findViewById(R.id.poster);

        //get passed data
        Intent intentThatCreatedThisActivity = getIntent();
        if(intentThatCreatedThisActivity != null){
            if(intentThatCreatedThisActivity.hasExtra("selected_movie")){

                //set data to views using passed parcelable data object
                movie =(MovieDetails)intentThatCreatedThisActivity.getParcelableExtra("selected_movie");
                Picasso.with(this).load(movie.image).into(movie_poster);
                titleText.setText(movie.title);
                releaseDate.setText(movie.releaseDate);
                voteAverage.setText(movie.voteAverage);
                plotSynopsis.setText(movie.description);
                //get movie id
                movieId=movie.movieId;
            }

        }
        try {
            //get trailer and review url for the selected movie
            URL[] trailers_review_url = NetworkUtils.trailer_review_url_builder(movieId);
            //get trailer Json Data
            String trailerJson = NetworkUtils.get_movie_details(trailers_review_url[0]);
            //filter trailer source data
            trailerUrls = MovieJsonUtils.get_trailer_values(trailerJson);
            //convert trailer sources into youtube urls
            movie = NetworkUtils.youtube_trailer_url_builder(trailerUrls, movie);

            String reviewJson = NetworkUtils.get_movie_details(trailers_review_url[1]);
            //fromat trailer json data into moviedata format
            movie = MovieJsonUtils.get_reviews_values(reviewJson, movie);
        }catch (java.io.IOException e){e.printStackTrace();
        }


    }





}
