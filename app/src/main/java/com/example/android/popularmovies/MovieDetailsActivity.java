package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.popularmovies.R.layout.activity_movie_details_activity;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieDetails> {
    private static final String SAVED_INSTANCE_KEY = "SAVED_INSTANCE";
    private static final int TRAILER_REVIEW_COLLECTION_LOADER_ID = 12;
    //used to identify and toggle img button
    private static final String BLACK_BTN_TAG = "BLACK_STAR";
    private static final String GOLDEN_BTN_TAG = "GOLDEN_STAR";
    String movieId;
    @BindView(R.id.loading_message)
    TextView mLoadingMessage;
    @BindView(R.id.basic_details_include)
    View mBasicDetailsLayout;
    @BindView(R.id.trailers_and_reviews_include)
    View mTrailerReviewLayout;
    @BindView(R.id.poster)
    ImageView mPoster;
    @BindView(R.id.movie_title)
    TextView mMovieTitle;
    @BindView(R.id.vote_average)
    TextView mVoterAverage;
    @BindView(R.id.release_date)
    TextView mReleaseDate;
    @BindView(R.id.plot_synopsis)
    TextView mPlotSynopsis;
    @BindView(R.id.author_1)
    TextView mAuthor1;
    @BindView(R.id.author_2)
    TextView mAuthor2;
    @BindView(R.id.author_3)
    TextView mAuthor3;
    @BindView(R.id.review_1)
    TextView mReview1;
    @BindView(R.id.review_2)
    TextView mReview2;
    @BindView(R.id.review_3)
    TextView mReview3;
    @BindView(R.id.trailer_btn_1)
    Button mTrailer1;
    @BindView(R.id.trailer_btn_2)
    Button mTrailer2;
    @BindView(R.id.trailer_btn_3)
    Button mTrailer3;
    @BindView(R.id.your_favorite_btn)
    ImageButton mFavButton;
    private MovieDetails movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity layout
        setContentView(activity_movie_details_activity);

        //set data binding layout
        ButterKnife.bind(this);
        //default tag
        mFavButton.setTag(BLACK_BTN_TAG);
        //restore data if device rotated
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_KEY)) {
            movie = savedInstanceState.getParcelable(SAVED_INSTANCE_KEY);
            //check if movie is already a favorite
            checkIfMovieIsFavorite(this);
        } else {
            //get passed data
            Intent receivedIntent = getIntent();
            if ((receivedIntent != null) && (receivedIntent.hasExtra("selected_movie"))) {
                //set data to views using passed parcelable data object
                movie = receivedIntent.getParcelableExtra("selected_movie");
                //get movie id
                movieId = movie.movieId;
                loadMovieTrailerReviews();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.movie_details_refresh_button) {
            loadMovieTrailerReviews();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieTrailerReviews() {
        // check for internet connection is available load trailers and movies
        if (NetworkUtils.checkInternetResource(this)) {
            mLoadingMessage.setVisibility(View.VISIBLE);
            mBasicDetailsLayout.setVisibility(View.GONE);
            mTrailerReviewLayout.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(TRAILER_REVIEW_COLLECTION_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, R.string.no_internet_for_trailer_review, Toast.LENGTH_LONG).show();
            checkIfMovieIsFavorite(this);
        }
    }

    // handle action when start button is pressed
    @OnClick(R.id.your_favorite_btn)
    public void favButtonClick() {

        /* if trailer and movie layout is not visible it would mean the data hasn't been fetched
           we do not want to store incomplete movie data as favourite even if the movie does'nt have trailer/reviews
        */
        if (mTrailerReviewLayout.getVisibility() == View.GONE) {
            Toast.makeText(this, R.string.no_internet_for_trailer_review, Toast.LENGTH_SHORT).show();
            return;
        }
        //create and enter movie data in a content values object to store or delete movie from favourites table
        ContentValues cp = new ContentValues();
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.movieId, movie.movieId);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.image, movie.image);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.movieTitle, movie.title);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.voteAverage, movie.voteAverage);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.releaseDate, movie.releaseDate);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.movieDescription, movie.description);

        cp.put(FavoriteMovieContract.FavoriteMovieEntry.author1, movie.author1);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.author2, movie.author2);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.author3, movie.author3);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl1, movie.trailerUrl1);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl2, movie.trailerUrl2);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl3, movie.trailerUrl3);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.review1, movie.review1);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.review2, movie.review2);
        cp.put(FavoriteMovieContract.FavoriteMovieEntry.review3, movie.review3);

        //get the selection
        String selectedImgSrc = mFavButton.getTag().toString();
        //insert details if movie was not favorite
        if (selectedImgSrc.equals(BLACK_BTN_TAG)) {
            Uri insertedUri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI, cp);
            if (insertedUri != null) {
                Toast.makeText(getBaseContext(), "Movie Added to Favourites", Toast.LENGTH_SHORT).show();
                bindData(GOLDEN_BTN_TAG);
                mFavButton.setTag(GOLDEN_BTN_TAG);
            }
        }
        //delete movie if movie was in table
        else if (selectedImgSrc.equals(GOLDEN_BTN_TAG)) {
            String selectionClause = FavoriteMovieContract.FavoriteMovieEntry.movieId + "=?";
            String[] selectionArgs = {movie.movieId};

            int deletedRows = getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI
                            .buildUpon().appendPath(FavoriteMovieContract.FavoriteMovieEntry.movieId).build(),
                    selectionClause,
                    selectionArgs);
            if (deletedRows >= 1) {
                Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                mFavButton.setTag(BLACK_BTN_TAG);
                bindData(BLACK_BTN_TAG);
            } else {
                Toast.makeText(getBaseContext(), "Not Removed from favorites", Toast.LENGTH_SHORT).show();
                mFavButton.setTag(GOLDEN_BTN_TAG);
                bindData(GOLDEN_BTN_TAG);
            }
        }
    }

    // method to bind data using Butter Knife
    private void bindData(String starTag) {

        //movie loaded remove loading message
        mLoadingMessage.setVisibility(View.GONE);
        mBasicDetailsLayout.setVisibility(View.VISIBLE);

        //set favButton
        if (starTag.equals(GOLDEN_BTN_TAG)) {
            mFavButton.setImageResource(R.drawable.ic_selected_star);
        } else {
            mFavButton.setImageResource(R.drawable.ic_unselected_star);
        }

        //start binding basic details which were fetched in main activity
        Picasso.with(this).load(movie.image).into(mPoster);
        mMovieTitle.setText(movie.title);
        mVoterAverage.setText(movie.voteAverage);
        mReleaseDate.setText(movie.releaseDate);
        mPlotSynopsis.setText(movie.description);

        /* If trailers and reviews are initialised or movie data is stored as favourite load the rest of data.
          Trailer and reviews will still be initialised even if there are no reviews/trailers for this movie.
        */
        if (movie.author1 != null) {
            mLoadingMessage.setVisibility(View.GONE);
            mTrailerReviewLayout.setVisibility(View.VISIBLE);
            mAuthor1.setText(movie.author1);
            mAuthor2.setText(movie.author2);
            mAuthor3.setText(movie.author3);
            mReview1.setText(movie.review1);
            mReview2.setText(movie.review2);
            mReview3.setText(movie.review3);

            //user shouldn't be able to click on trailer if not available
            if (movie.trailerUrl1.isEmpty()) {
                mTrailer1.setVisibility(View.GONE);
            }
            if (movie.trailerUrl2.isEmpty()) {
                mTrailer2.setVisibility(View.GONE);
            }
            if (movie.trailerUrl3.isEmpty()) {
                mTrailer3.setVisibility(View.GONE);
            }
        } else {
            //since the trailers/reviews are'nt fetched we would hide trailer/review layout
            mLoadingMessage.setText(R.string.no_internet_for_trailer_review);
            mLoadingMessage.setVisibility(View.VISIBLE);
            mTrailerReviewLayout.setVisibility(View.GONE);
        }
    }

    //handle trailer buttons
    public void trailerButtonClicked(View view) {
        //find out which button clicked
        int buttonId = view.getId();
        Intent trailerIntent;
        switch (buttonId) {
            case R.id.trailer_btn_1:
                trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl1));
                startActivity(trailerIntent);
                break;
            case R.id.trailer_btn_2:
                trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl2));
                startActivity(trailerIntent);
                break;
            case R.id.trailer_btn_3:
                trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailerUrl3));
                startActivity(trailerIntent);
                break;
        }
    }

    //check whether the movie is already favourite and bind data
    private void checkIfMovieIsFavorite(Context context) {
        AsyncTaskLoader checkTask = new AsyncTaskLoader<Cursor>(context) {
            Cursor retCursor;

            @Override
            protected void onStartLoading() {
                if (retCursor != null)
                    deliverResult(retCursor);
                else
                    forceLoad();
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    //create parameters for query method
                    String selectionClause = FavoriteMovieContract.FavoriteMovieEntry.movieId + "=?";
                    String[] selectionArgs = {movie.movieId};

                    retCursor = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI
                                    .buildUpon().appendPath(FavoriteMovieContract.FavoriteMovieEntry.movieId).build(),
                            null,
                            selectionClause,
                            selectionArgs,
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return retCursor;
            }

            @Override
            public void deliverResult(Cursor retCursor) {
                if ((retCursor != null) && (retCursor.getCount() > 0)) {
                    mFavButton.setTag(GOLDEN_BTN_TAG);
                    bindData(GOLDEN_BTN_TAG);
                } else {
                    mFavButton.setTag(BLACK_BTN_TAG);
                    bindData(BLACK_BTN_TAG);
                }
                super.deliverResult(retCursor);
            }
        };
        checkTask.startLoading();
    }

    // implement saved instance to store the current movie for next state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_INSTANCE_KEY, movie);
    }

    // handle appropriate async task for trailer and review collection
    @Override
    public Loader<MovieDetails> onCreateLoader(int id, Bundle args) {
        if (id == TRAILER_REVIEW_COLLECTION_LOADER_ID) {
            return new AsyncTaskForTrailerReviewCollection(this, movie);
        } else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<MovieDetails> loader, MovieDetails returnedMovie) {
        movie = returnedMovie;
        //check if movie is already favourite and bind it
        checkIfMovieIsFavorite(this);
    }

    @Override
    public void onLoaderReset(Loader<MovieDetails> loader) {
    }
}
