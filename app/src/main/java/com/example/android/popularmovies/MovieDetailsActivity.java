package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.databinding.ActivityMovieDetailsActivityBinding;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.R.layout.activity_movie_details_activity;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String SAVED_INSTANCE_KEY="SAVED_INSTANCE";
    String movieId;
    private Button trailer1,trailer2,trailer3;
    //used to identify and toggle img button
    private static final String BLACK_BTN_TAG="BLACK_STAR";
    private static final String GOLDEN_BTN_TAG="GOLDEN_STAR";
    private ImageButton favButton;

    private ActivityMovieDetailsActivityBinding mBinding;
    MovieDetails movie ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity layout
        setContentView(activity_movie_details_activity);
    if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){

}

        trailer1 = (Button) findViewById(R.id.trailer_btn_1);
        trailer2 = (Button) findViewById(R.id.trailer_btn_2);
        trailer3 = (Button) findViewById(R.id.trailer_btn_3);
        //default
        favButton = (ImageButton) findViewById((R.id.your_favorite_btn));
       favButton.setTag(BLACK_BTN_TAG);
       // favButton.setImageResource(R.drawable.ic_black_star);
        //set data binding layout
        mBinding = DataBindingUtil.setContentView(this, activity_movie_details_activity);

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
                final Context mcontext = this;
                    // start async task to collect trailer and review data
                     new AsycTaskForTrailerReviewCollection() {
                        @Override
                        protected void onPostExecute(MovieDetails selectedMovie) {
                            super.onPostExecute(selectedMovie);
                            //set the movie data to the updated version of movie data which includes trailers and reviews
                            movie = selectedMovie;
                            checkIfMovieIsFavorite(mcontext);
                        }
                    }.execute(movie);
            }
        }
        checkIfMovieIsFavorite(this);

    }

    public void favButtonClick(View view){
        //create and enter movie data in a content values object to store or delete movie from favorities table
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
        String selectedImgSrc =  favButton.getTag().toString();
        //insert details if movie was not favorite
        if(selectedImgSrc == BLACK_BTN_TAG){
            Uri insertedUri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,cp);
            if(insertedUri!=null){
                Toast.makeText(getBaseContext(),insertedUri.toString(),Toast.LENGTH_SHORT).show();
                bindData(GOLDEN_BTN_TAG);
               favButton.setTag(GOLDEN_BTN_TAG);
                //favButton.setImageResource(R.drawable.ic_gold_star);
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
               // favButton.setImageResource(R.drawable.ic_black_star);
                bindData(BLACK_BTN_TAG);
            }else{
                Toast.makeText(getBaseContext(),"Not Removed from favorites",Toast.LENGTH_SHORT).show();
                favButton.setTag(GOLDEN_BTN_TAG);
               // favButton.setImageResource(R.drawable.ic_gold_star);
                bindData(GOLDEN_BTN_TAG);
            }
        }


    }



    public void bindData(String starTag) {
        mBinding = DataBindingUtil.setContentView(this, activity_movie_details_activity);
        mBinding.trailersAndReviewsInclude.setMovie(movie);
        mBinding.basicDetailsInclude.setMovie(movie);

        if(starTag.equals(GOLDEN_BTN_TAG)){
            mBinding.basicDetailsInclude.yourFavoriteBtn.setImageResource(R.drawable.ic_gold_star);
        }
        else{
            mBinding.basicDetailsInclude.yourFavoriteBtn.setImageResource(R.drawable.ic_black_star);
        }
        //set poster image
        Picasso.with(this).load(movie.image).into(mBinding.basicDetailsInclude.poster);
        if (movie.trailerUrl1 == null) {
            mBinding.trailersAndReviewsInclude.trailerBtn1.setActivated(false);
        }
        if (movie.trailerUrl2 == null) {
            mBinding.trailersAndReviewsInclude.trailerBtn2.setActivated(false);
        }
        if (movie.trailerUrl3 == null) {
            mBinding.trailersAndReviewsInclude.trailerBtn3.setActivated(false);
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

                retCursor= getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI
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

                favButton.setTag(GOLDEN_BTN_TAG);
               // favButton.setImageResource(R.drawable.ic_gold_star);
                Log.d("Favorite Selection","Movie is already in Favorites");
                bindData(GOLDEN_BTN_TAG);
            }else{
                favButton.setTag(BLACK_BTN_TAG);
             //  favButton.setImageResource(R.drawable.ic_black_star);
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
