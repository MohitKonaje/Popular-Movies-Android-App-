package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.databinding.ActivityMovieDetailsActivityBinding;
import com.squareup.picasso.Picasso;


import static com.example.android.popularmovies.R.layout.activity_movie_details_activity;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String SAVED_INSTANCE_KEY="SAVED_INSTANCE";
    String movieId;
    private ActivityMovieDetailsActivityBinding mBinding;
    MovieDetails movie ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set activity layout
        setContentView(activity_movie_details_activity);
        //set data binding layout
        mBinding = DataBindingUtil.setContentView(this,activity_movie_details_activity);

        if(savedInstanceState!= null&& savedInstanceState.containsKey(SAVED_INSTANCE_KEY))
        {
            movie=savedInstanceState.getParcelable(SAVED_INSTANCE_KEY);
                bindData();
        }
        else {
            //get passed data
            Intent intentThatCreatedThisActivity = getIntent();
            if (intentThatCreatedThisActivity != null) {
                if (intentThatCreatedThisActivity.hasExtra("selected_movie")) {

                    //set data to views using passed parcelable data object
                    movie = (MovieDetails) intentThatCreatedThisActivity.getParcelableExtra("selected_movie");
                    //get movie id
                    movieId = movie.movieId;
                    // start async task to collect trailer and review data
                    new AsycTaskForTrailerReviewCollection() {
                        @Override
                        protected void onPostExecute(MovieDetails selectedMovie) {
                            super.onPostExecute(selectedMovie);
                            //set the movie data to the updated version of movie data which includes trailers and reviews
                            movie = selectedMovie;
                            bindData();
                        }
                    }.execute(movie);
                }
            }
        }

    }

    public void bindData(){

        this.mBinding = DataBindingUtil.setContentView(this,activity_movie_details_activity);
        //set poster image
        Picasso.with(this).load(movie.image).into(mBinding.basicDetailsInclude.poster);
        //bind basic details data
        this.mBinding.basicDetailsInclude.movieTitle.setText(movie.title);
        this.mBinding.basicDetailsInclude.releaseDate.setText(movie.releaseDate);
        mBinding.basicDetailsInclude.voteAverage.setText(movie.voteAverage);
        mBinding.basicDetailsInclude.plotSynopsis.setText(movie.description);

        //bind trailer and review data

        mBinding.trailersAndReviewsInclude.author1.setText(movie.author1);
        mBinding.trailersAndReviewsInclude.author2.setText(movie.author2);
        mBinding.trailersAndReviewsInclude.author3.setText(movie.author3);
        mBinding.trailersAndReviewsInclude.review1.setText(movie.review1);
        mBinding.trailersAndReviewsInclude.review2.setText(movie.review2);
        mBinding.trailersAndReviewsInclude.review3.setText(movie.review3);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_INSTANCE_KEY,movie);
    }
}
