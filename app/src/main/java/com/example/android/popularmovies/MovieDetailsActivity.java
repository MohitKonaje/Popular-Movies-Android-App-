package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieContract;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.R.layout.activity_movie_details_activity;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String SAVED_INSTANCE_KEY="SAVED_INSTANCE";
    String movieId;

    @BindView(R.id.poster) ImageView mPoster;

    @BindView(R.id.movie_title) TextView mMovieTitle;
    @BindView(R.id.vote_average) TextView mVoterAverage;
    @BindView(R.id.release_date) TextView mReleaseDate;
    @BindView(R.id.plot_synopsis) TextView mPlotSynopsis;

    @BindView(R.id.author_1) TextView mAuthor1;
    @BindView(R.id.author_2) TextView mAuthor2;
    @BindView(R.id.author_3) TextView mAuthor3;
    @BindView(R.id.review_1) TextView mReview1;
    @BindView(R.id.review_2) TextView mReview2;
    @BindView(R.id.review_3) TextView mReview3;

    @BindView(R.id.trailer_btn_1) Button mTrailer1;
    @BindView(R.id.trailer_btn_2) Button mTrailer2;
    @BindView(R.id.trailer_btn_3) Button mTrailer3;

    @BindView(R.id.your_favorite_btn) ImageButton mFavButton;



    //used to identify and toggle img button
    private static final String BLACK_BTN_TAG="BLACK_STAR";
    private static final String GOLDEN_BTN_TAG="GOLDEN_STAR";

    MovieDetails movie ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity layout
        setContentView(activity_movie_details_activity);

        //set data binding layout
        ButterKnife.bind(this);
        //default tag
        mFavButton.setTag(BLACK_BTN_TAG);
        //check if movie is already a favorite
        //checkIfMovieIsFavorite(this);
        //restore data if device rotated
        if(savedInstanceState!= null&& savedInstanceState.containsKey(SAVED_INSTANCE_KEY))
        {
            movie=savedInstanceState.getParcelable(SAVED_INSTANCE_KEY);

            checkIfMovieIsFavorite(this);
        }
        else
        {
            //get passed data
            Intent intentThatCreatedThisActivity = getIntent();
            if ((intentThatCreatedThisActivity != null)&& (intentThatCreatedThisActivity.hasExtra("selected_movie"))) {
                //set data to views using passed parcelable data object
                    movie = (MovieDetails) intentThatCreatedThisActivity.getParcelableExtra("selected_movie");
                    //get movie id
                    movieId = movie.movieId;
                //context for AsyncTask
                final Context mContext = this;
                    // start async task to collect trailer and review data
                     new AsyncTaskForTrailerReviewCollection() {
                        @Override
                        protected void onPostExecute(MovieDetails selectedMovie) {
                            super.onPostExecute(selectedMovie);
                            //set the movie data to the updated version of movie data which includes trailers and reviews
                            movie = selectedMovie;
                            checkIfMovieIsFavorite(mContext);
                        }
                    }.execute(movie);
            }
        }
        checkIfMovieIsFavorite(this);

    }

    public void favButtonClick(View view){
        //create and enter movie data in a content values object to store or delete movie from favourites table
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
        String selectedImgSrc =  mFavButton.getTag().toString();
        //insert details if movie was not favorite
        if(selectedImgSrc.equals(BLACK_BTN_TAG)){
            Uri insertedUri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI,cp);
            if(insertedUri!=null){
                Toast.makeText(getBaseContext(),"Movie Added to Favourites",Toast.LENGTH_SHORT).show();
                bindData(GOLDEN_BTN_TAG);
               mFavButton.setTag(GOLDEN_BTN_TAG);

            }
        }

        //delete movie if movie was in table
        else if(selectedImgSrc.equals(GOLDEN_BTN_TAG)){
            String selectionClause=FavoriteMovieContract.FavoriteMovieEntry.movieId+"=?";
            String[] selectionArgs={movie.movieId};

            int deletedRows= getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI
                            .buildUpon().appendPath(FavoriteMovieContract.FavoriteMovieEntry.movieId).build(),
                    selectionClause,
                    selectionArgs);
            if(deletedRows>=1)
            {
                Toast.makeText(getBaseContext(),"Removed from favorites",Toast.LENGTH_SHORT).show();
                mFavButton.setTag(BLACK_BTN_TAG);
                bindData(BLACK_BTN_TAG);
            }else{
                Toast.makeText(getBaseContext(),"Not Removed from favorites",Toast.LENGTH_SHORT).show();
                mFavButton.setTag(GOLDEN_BTN_TAG);
                bindData(GOLDEN_BTN_TAG);
            }
        }


    }



    public void bindData(String starTag) {

        mMovieTitle.setText(movie.title);
        mVoterAverage.setText(movie.voteAverage);
        mReleaseDate.setText(movie.releaseDate);
        mPlotSynopsis.setText(movie.description);

        mAuthor1.setText(movie.author1);
        mAuthor2.setText(movie.author2);
        mAuthor3.setText(movie.author3);
        mReview1.setText(movie.review1);
        mReview2.setText(movie.review2);
        mReview3.setText(movie.review3);





        if(starTag.equals(GOLDEN_BTN_TAG)){
            mFavButton.setImageResource(R.drawable.ic_gold_star);
        }
        else{
            mFavButton.setImageResource(R.drawable.ic_black_star);

        }
        //set poster image
        Picasso.with(this).load(movie.image).into(mPoster);
        if (movie.trailerUrl1 == null) {
            mTrailer1.setClickable(false);
        }
        if (movie.trailerUrl2 == null) {
            mTrailer2.setClickable(false);

        }
        if (movie.trailerUrl3 == null) {
            mTrailer3.setClickable(false);

        }
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


private void checkIfMovieIsFavorite(Context context){
    AsyncTaskLoader checkTask = new AsyncTaskLoader<Cursor>(context) {
        Cursor retCursor;

        @Override
        protected void onStartLoading() {
            if(retCursor!=null)
                deliverResult(retCursor);
            else
                forceLoad();
        }

        @Override
        public Cursor loadInBackground() {

            try {
                //create parameters for query method
                String selectionClause=FavoriteMovieContract.FavoriteMovieEntry.movieId+"=?";
                String[] selectionArgs={movie.movieId};

                retCursor= getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI
                                .buildUpon().appendPath(FavoriteMovieContract.FavoriteMovieEntry.movieId).build(),
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
        public void deliverResult(Cursor retCursor) {
            if ((retCursor != null) && (retCursor.getCount()>0)) {

                mFavButton.setTag(GOLDEN_BTN_TAG);

                Log.d("Favorite Selection","Movie is already in Favorites");
                bindData(GOLDEN_BTN_TAG);
            }else{
                mFavButton.setTag(BLACK_BTN_TAG);

                Log.d("Favorite Selection","Movie is not in Favorites");
                bindData(BLACK_BTN_TAG);
            }
            super.deliverResult(retCursor);
        }

    };
 checkTask.startLoading();
}



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_INSTANCE_KEY,movie);
    }


}
