package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.movieId;


public class MainActivity extends AppCompatActivity implements MovieGridAdapter.movieGridAdapterOnClickHandler {

    private static final String LOG = MainActivity.class.getSimpleName();
    private static final int MOST_POPULAR_SORT = 0;
    private static final int TOP_RATED_SORT = 1;
    private static final int FAVOURITE_SORT = 2;
    //key for bundle to store the default sort selection value during activity creation/destruction
    private static final String SORT_SELECTION_KEY = "SELECTION_KEY";
    //key to restore recycler view scroll state
    private static final String RECYCLER_VIEW_STATE = "RECYCLER_VIEW_STATE";
    private static final String SAVED_STATE_MOVIES_DATA = "SAVED_STATE_MOVIES_DATA";
    MovieGridAdapter mAdapter;
    @BindView(R.id.poster_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.error_message_display)
    TextView mErrorTextView;
    @BindView(R.id.loading_bar)
    ProgressBar mLoadBar;
    private GridLayoutManager mLayout;
    private int selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///set main layout
        setContentView(R.layout.activity_main);
        //initialise adapter
        mAdapter = new MovieGridAdapter(this, this);
        //span more columns if in landscape mode
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //set layout to recyclerView
            mLayout = new GridLayoutManager(this, 3);
        } else {
            mLayout = new GridLayoutManager(this, 2);
        }
        //initialize views in this activity
        ButterKnife.bind(this);
        //set fixed size
        mRecyclerView.setHasFixedSize(true);
        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_SELECTION_KEY)) {
            //set sort selection key
            selection = savedInstanceState.getInt(SORT_SELECTION_KEY);
            Parcelable scrollState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayout.onRestoreInstanceState(scrollState);
            //setting grid layout to recyclerView
            mRecyclerView.setLayoutManager(mLayout);
            //setting adapter
            mRecyclerView.setAdapter(mAdapter);
            /*if you rotate the phone in details screen and press back button the app should reload
            favourite movie data for updated screen */
            if (selection == FAVOURITE_SORT) {
                loadMovieData(selection);
            } else {
                MovieDetails[] restoredMovieData = (MovieDetails[]) savedInstanceState.getParcelableArray(SAVED_STATE_MOVIES_DATA);
                if (restoredMovieData == null) {
                    mErrorTextView.setText(getString(R.string.no_movie_to_load));
                }
                mAdapter.setData(restoredMovieData);
            }
            Log.i(LOG, "Restoring Recycler View from saved instance state");
        } else {
            //set sort selection
            selection = MOST_POPULAR_SORT; // default
            //setting grid layout to recyclerView
            mRecyclerView.setLayoutManager(mLayout);
            //setting adapter
            mRecyclerView.setAdapter(mAdapter);
            loadMovieData(selection);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadMovieData(selection);
    }

    private void loadMovieData(int selection) {
        //loading favourite movies do not require internet
        if (selection == FAVOURITE_SORT) {
            loadFavouriteMovieData(this);
            return;
        }

        // start collecting data only in internet connection is available
        if (NetworkUtils.checkInternetResource(this)) {
            //make loading message Visible
            mLoadBar.setVisibility(View.VISIBLE);
            mErrorTextView.setText(getString(R.string.loading_text));
            mErrorTextView.setVisibility(View.VISIBLE);
            // build url to fetch movie data
            URL link = NetworkUtils.url_builder(selection);
            //start a new Async task to fetch data
            new AsyncTaskForMovieDataCollection(this, new FetchMovieData()).execute(link);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mErrorTextView.setText(getString(R.string.no_internet_error));
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(MovieDetails selected_movie) {
        Class movie_details_activity = MovieDetailsActivity.class;
        //create new intent for detail activity
        Intent detailsIntent = new Intent(this, movie_details_activity);
        //pass parcelable data
        detailsIntent.putExtra("selected_movie", selected_movie);
        startActivity(detailsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get sort option
        int MenuItemSelection = item.getItemId();

        switch (MenuItemSelection) {
            case R.id.most_popular:
                this.selection = MOST_POPULAR_SORT;
                // rearrange by most popular
                loadMovieData(selection);
                break;
            case R.id.top_rated:
                this.selection = TOP_RATED_SORT;
                //rearrange by rating
                loadMovieData(selection);
                break;
            case R.id.favorites:
                this.selection = FAVOURITE_SORT;
                //rearrange by favorite
                loadMovieData(selection);
                break;
            case R.id.refresh_button:
                //refresh poster layout of current sort selection;
                loadMovieData(selection);
                break;
            case R.id.reset_favorites:
                //delete all movies in favorites directory
                int deletedRows = getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI,
                        null,
                        null);
                if (deletedRows > 0) {
                    Toast.makeText(this, deletedRows + " Movies deleted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "No movies to be deleted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Storing data in SaveInstanceState Bundle to maintain sorting selection in Landscape and portrait mode
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_SELECTION_KEY, this.selection);
        //send movie data
        outState.putParcelableArray(SAVED_STATE_MOVIES_DATA, mAdapter.mDataItems);
        //store recycler view state
        outState.putParcelable(RECYCLER_VIEW_STATE, mLayout.onSaveInstanceState());
        //store selection
        super.onSaveInstanceState(outState);
    }

    private void loadFavouriteMovieData(Context context) {
        AsyncTaskLoader<Cursor> asyncTask = new AsyncTaskLoader<Cursor>(context) {
            Cursor retCursor;

            @Override
            protected void onStartLoading() {
                if (retCursor != null) {
                    deliverResult(retCursor);
                }
                // no favourite movies to be loaded show no movies message
                else {
                    mErrorTextView.setText(getString(R.string.no_fav_movie_stored));
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mAdapter.setData(null);
                }
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return retCursor = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVOURITE_MOVIES_CONTENT_URI,
                            null,
                            null,
                            null,
                            movieId);
                } catch (Exception e) {
                    Log.e(LOG, "failed to load fav movie data");
                    e.printStackTrace();

                    mRecyclerView.setVisibility(View.GONE);
                    mErrorTextView.setText(getString(R.string.error_string));
                    mErrorTextView.setVisibility(View.VISIBLE);
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor retCursor) {
                if (retCursor.getCount() > 0) {
                    bindFavMovies(retCursor);
                }
                super.deliverResult(retCursor);
            }

            private void bindFavMovies(Cursor retCursor) {
                MovieDetails[] favoriteMoviesData = new MovieDetails[retCursor.getCount()];
                int i = 0;
                //store cursor data in MovieData format
                retCursor.moveToFirst();
                while (i < retCursor.getCount()) {
                    String title = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieTitle));
                    String releaseDate = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.releaseDate));
                    String voteAverage = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.voteAverage));
                    String description = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieDescription));
                    String image = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.image));
                    String id = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieId));

                    String trailerUrl1 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl1));
                    String trailerUrl2 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl2));
                    String trailerUrl13 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl3));
                    String author1 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.author1));
                    String author2 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.author2));
                    String author3 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.author3));
                    String review1 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.review1));
                    String review2 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.review2));
                    String review3 = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.review3));
                    //insert fetched values in the MovieData array
                    favoriteMoviesData[i] = new MovieDetails(title, releaseDate, voteAverage, description, image, id,
                            trailerUrl1, trailerUrl2, trailerUrl13, author1, author2, author3, review1, review2, review3);
                    i++;
                    retCursor.moveToNext();
                }
                //handle loadingBar
                mRecyclerView.setVisibility(View.VISIBLE);
                mErrorTextView.setVisibility(View.INVISIBLE);
                mAdapter.setData(favoriteMoviesData);
            }
        };
        asyncTask.startLoading();
    }

    public class FetchMovieData implements InterfaceListener.onTaskCompleteListener<MovieDetails[]> {
        //handle data from async task
        @Override
        public void onTaskComplete(MovieDetails[] parsedResults) {
            //make loadBar invisible
            mLoadBar.setVisibility(View.GONE);
            //check if data is not null
            if (parsedResults != null) {
                //handle loadingBar
                mRecyclerView.setVisibility(View.VISIBLE);
                mErrorTextView.setVisibility(View.INVISIBLE);
                //pass data to set data function
                mAdapter.setData(parsedResults);
            }
        }
    }
}


