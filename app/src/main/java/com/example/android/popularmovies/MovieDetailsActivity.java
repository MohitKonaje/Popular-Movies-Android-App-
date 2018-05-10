package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.databinding.ActivityMovieDetailsActivityBinding;
import com.squareup.picasso.Picasso;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.android.popularmovies.R.layout.activity_movie_details_activity;

public class MovieDetailsActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SAVED_INSTANCE_KEY="SAVED_INSTANCE";
    private static final int LOADER_ID_ONE_MOVIE = 1;
    String movieId;
    private Button trailer1,trailer2,trailer3;
    //used to identify and toggle img button
    private static final String BLACK_BTN_TAG="BLACK_STAR";
    private static final String GOLDEN_BTN_TAG="GOLDEN_STAR";
    private ImageButton favButton;

    public ActivityMovieDetailsActivityBinding mBinding;
    MovieDetails movie ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity layout
        setContentView(activity_movie_details_activity);
if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){

}
        //set data binding layout
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_movie_details_activity);
        trailer1 = (Button) findViewById(R.id.trailer_btn_1);
        trailer2 = (Button) findViewById(R.id.trailer_btn_2);
        trailer3 = (Button) findViewById(R.id.trailer_btn_3);
        favButton = (ImageButton) findViewById((R.id.your_favorite_btn));
        favButton.setTag(BLACK_BTN_TAG);

        //check if movie is already a favorite
   //     getSupportLoaderManager().initLoader(LOADER_ID_ONE_MOVIE,null,this);

        //restore data if device rotated
        if(savedInstanceState!= null&& savedInstanceState.containsKey(SAVED_INSTANCE_KEY))
        {
            movie=savedInstanceState.getParcelable(SAVED_INSTANCE_KEY);
                bindData();
        }
        else
        {
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

        favButton.setOnClickListener(new View.OnClickListener() {
            //create and enter movie data in a content values object to store or delete movie from favorities table
            @Override
            public void onClick(View view) {
                ContentValues cp = new ContentValues();
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.movieId,movie.movieId);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.image,movie.image);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.movieTitle,movie.title);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.voteAverage,movie.voteAverage);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.releaseDate,movie.releaseDate);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.movieDescription,movie.description);

                cp.put(FavoriteMovieContract.FavoriteMovieEntry.author1,movie.author1);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.author2,movie.author2);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.author3,movie.author3);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl1,movie.trailerUrl1);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl2,movie.trailerUrl2);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl3,movie.trailerUrl3);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.review1,movie.review1);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.review2,movie.review2);
                cp.put(FavoriteMovieContract.FavoriteMovieEntry.review3,movie.review3);

                //get the selection

                String selectedImgSrc = favButton.getTag().toString();
                //insert details if movie was not favorite
                if(selectedImgSrc == BLACK_BTN_TAG){
                    Uri insertedUri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,cp);
                    if(insertedUri!=null){
                         Toast.makeText(getBaseContext(),insertedUri.toString(),Toast.LENGTH_SHORT).show();
                        favButton.setTag(GOLDEN_BTN_TAG);
                        favButton.setImageResource(R.drawable.ic_gold_star);
                    }
                }

                //delete movie if movie was in table
                else if(selectedImgSrc == GOLDEN_BTN_TAG){
                    String selectionClause=FavoriteMovieContract.FavoriteMovieEntry.movieId+"=?";
                    String[] selectionArgs={movie.movieId};

                    int deletedrows= getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI
                                    .buildUpon().appendPath(FavoriteMovieContract.FavoriteMovieEntry.movieId).build(),
                           selectionClause,
                            selectionArgs);
                    if(deletedrows>=1)
                    {
                        Toast.makeText(getBaseContext(),"Removed from favorites",Toast.LENGTH_SHORT).show();
                        favButton.setTag(BLACK_BTN_TAG);
                        favButton.setImageResource(R.drawable.ic_black_star);
                    }else{
                        Toast.makeText(getBaseContext(),"Not Removed from favorites",Toast.LENGTH_SHORT).show();
                        favButton.setTag(GOLDEN_BTN_TAG);
                        favButton.setImageResource(R.drawable.ic_gold_star);
                    }
                    }



            }
        });

    }


    public void bindData() {


        mBinding.trailersAndReviewsInclude.setMovie(movie);
        mBinding.basicDetailsInclude.setMovie(movie);
        //set poster image
        Picasso.with(this).load(movie.image).into(mBinding.basicDetailsInclude.poster);
        if (movie.trailerUrl1 == null) {
            trailer1.setActivated(false);
        }
        if (movie.trailerUrl2 == null) {
            trailer1.setActivated(false);
        }
        if (movie.trailerUrl3 == null) {
            trailer1.setActivated(false);
        }

        //bind basic details data
       /* mBinding.basicDetailsInclude.movieTitle.setText(movie.title);
        mBinding.basicDetailsInclude.releaseDate.setText(movie.releaseDate);
        mBinding.basicDetailsInclude.voteAverage.setText(movie.voteAverage);
        mBinding.basicDetailsInclude.plotSynopsis.setText(movie.description);

        //bind trailer and review data

        mBinding.trailersAndReviewsInclude.author1.setText(movie.author1);
        mBinding.trailersAndReviewsInclude.author2.setText(movie.author2);
        mBinding.trailersAndReviewsInclude.author3.setText(movie.author3);
        mBinding.trailersAndReviewsInclude.review1.setText(movie.review1);
        mBinding.trailersAndReviewsInclude.review2.setText(movie.review2);
        mBinding.trailersAndReviewsInclude.review3.setText(movie.review3);
        */
    }
   public void trailer1(View view){
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl1));
       startActivity(trailerIntent);
    }
    public void trailer2(View view){
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl2));
        startActivity(trailerIntent);
    }
    public void trailer3(View view){
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl3));
        startActivity(trailerIntent);
    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor retCursor;

            @Override
            protected void onStartLoading() {
                if(retCursor==null)
                    forceLoad();
                else
                    deliverResult(retCursor);
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    //create parameters for query method
                    String selectionClause=FavoriteMovieContract.FavoriteMovieEntry.movieId+"=?";
                    String[] selectionArgs={movie.movieId};

                    retCursor= getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI
                                    .buildUpon().appendPath(FavoriteMovieContract.FavoriteMovieEntry.movieId).build()
                                    ,
                            null,
                            selectionClause,
                            selectionArgs,
                            null);
                }catch(Exception e)
                {
                    Log.e("PROVIDER_ERROR","failed to load fav movie data");
                    e.printStackTrace();
                    return null;
                }

                return retCursor;

            }

            @Override
            public void deliverResult(Cursor data) {
                retCursor=data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor queryResult) {
//set button to gold star if movie exist in db
        if(queryResult !=null){
            queryResult.moveToFirst();

        //    Log.d("Test",""+queryResult.getString(queryResult.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieId)));
            favButton.setTag(GOLDEN_BTN_TAG);
            favButton.setImageResource(R.drawable.ic_gold_star);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.forceLoad();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_INSTANCE_KEY,movie);
    }

}
