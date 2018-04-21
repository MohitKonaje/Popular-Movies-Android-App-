package com.example.android.popularmovies;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.net.URL;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;


public class MainActivity  extends AppCompatActivity implements MovieGridAdapter.movieGridAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks {

    RecyclerView recyclerView;
    TextView errorTextView;
    ProgressBar loadBar;
    //key for bundle to store the default sort selection value during activity creation/destruction
    public final String SORT_SELECTION_KEY="SELECTION_KEY";
    //load id for all favorite movies
    private static final int LOADER_ID_All_MOVIES = 0;
    private static final int LOADER_ID_DELETE_ALL_MOVIES=2;
    MovieGridAdapter mAdapter;
    int selection;
    private MovieDetails[] favoriteMoviesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///set main layout
        setContentView(R.layout.activity_main);


        //initialise loading bar
        loadBar = (ProgressBar) findViewById(R.id.loading_bar);

        //initialise textView to display error
        errorTextView = (TextView) findViewById(R.id.error_message_display);

        //create recyclerView
         recyclerView = (RecyclerView) findViewById(R.id.poster_recycler_view);

        GridLayoutManager mLayout = new GridLayoutManager(this, 2);

        //span more columns if in landscape mode
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //set layout to recyclerView
             mLayout = new GridLayoutManager(this, 3);
        }

        //set fixed size
        recyclerView.setHasFixedSize(true);

        //setting grid layout to recyclerView
        recyclerView.setLayoutManager(mLayout);

        if(savedInstanceState !=null)
        {
        if(savedInstanceState.containsKey(SORT_SELECTION_KEY)){
        selection= savedInstanceState.getInt(SORT_SELECTION_KEY);
        }
        }
        else {
            //set sort selection
            selection = 0; // default

        }

        //initialise adapter
        mAdapter = new MovieGridAdapter(this,this);


        //setting adapter
        recyclerView.setAdapter(mAdapter);
        loadMovieData(selection);
    }



    void loadMovieData(int selection){
        //make loading bar Visible
        loadBar.setVisibility(View.VISIBLE);
        errorTextView.setText("Loading");
        errorTextView.setVisibility(View.VISIBLE);

        if(selection == 2) {
            getSupportLoaderManager().initLoader(LOADER_ID_All_MOVIES, null, this);
            return;
        }

        // check for internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE) ;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // start collecting data only in internet connection is available
        if(networkInfo !=null ){
            // build url to fetch movie data
            URL link = NetworkUtils.url_builder(selection);
            //start a new Async task to fetch data
            new AsyncTaskForMovieDataCollection(this,new FetchMovieData()).execute(link);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);
            errorTextView.setText("No Internet Connection");
            errorTextView.setVisibility(View.VISIBLE);

        }






    }


    @Override
    public void onClick(MovieDetails selected_movie) {
        Class movie_details_activity = MovieDetailsActivity.class;
        //create new intent for detail activity
        Intent detailsIntent = new Intent(this,movie_details_activity);
        //pass parcelable data
        detailsIntent.putExtra("selected_movie",(Parcelable) selected_movie);
        startActivity(detailsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get sort option
        int MenuItemSelection = item.getItemId();

        if(MenuItemSelection == R.id.most_popular){
            this.selection=0;
            // rearrange by most popular
            loadMovieData(selection);

        }
        else if(MenuItemSelection == R.id.top_rated){
            this.selection=1;
            //rearrange by rating
            loadMovieData(selection);


        }
        else if(MenuItemSelection == R.id.favorites){
            this.selection=2;
            //rearrange by favorite
            loadMovieData(selection);

        }
        else if (MenuItemSelection == R.id.refresh_button){
            //refresh poster layout of current sort selection;
            loadMovieData(selection);

        }
        else if (MenuItemSelection == R.id.reset_favorites){
            //delete all movies in favorites directory
            getSupportLoaderManager().initLoader(LOADER_ID_DELETE_ALL_MOVIES, null, this);

        }

        return super.onOptionsItemSelected(item);
    }


    //Storing data in SaveInstanceState Bundle to maintain sorting selection in Landscape and portrait mode
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_SELECTION_KEY,this.selection);

    }


    public class FetchMovieData implements InterfaceListener.onTaskCompleteListener<MovieDetails[]>{

        //handle data from async task
        @Override
        public void onTaskComplete(MovieDetails[] parsedResults) {
            //make loadBar invisible
            loadBar.setVisibility(View.INVISIBLE);

            //check if data is not null
            if(parsedResults!= null){

                //handle loadingBar
                recyclerView.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.INVISIBLE);

                //pass data to set data function
                mAdapter.setData(parsedResults);
            }
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id==LOADER_ID_All_MOVIES) {


            return new AsyncTaskLoader<Cursor>(this) {
                Cursor returnCursor;

                @Override
                protected void onStartLoading() {

                    //check if fav movies loaded
                    if (returnCursor != null) {
                        deliverResult(returnCursor);
                    }
                    //else load fav movies
                    else {
                        forceLoad();
                    }

                }


                @Override
                public Cursor loadInBackground() {

                    try {
                        return returnCursor = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,
                                null,
                                null,
                                null,
                                FavoriteMovieContract.FavoriteMovieEntry.movieId);
                    } catch (Exception e) {
                        Log.e("PROVIDER_ERROR", "failed to load fav movie data");
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor favMovieData) {
                    ContentValues cp = new ContentValues();
                    if (favMovieData != null) {
                        int i = 0;
                        while (favMovieData.moveToNext()) {
                            favoriteMoviesData[i].movieId = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.movieId);
                            favoriteMoviesData[i].image = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.image);
                            favoriteMoviesData[i].title = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.movieTitle);
                            favoriteMoviesData[i].voteAverage = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.voteAverage);
                            favoriteMoviesData[i].releaseDate = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.releaseDate);
                            favoriteMoviesData[i].description = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.movieDescription);

                            favoriteMoviesData[i].author1 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.author1);
                            favoriteMoviesData[i].author2 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.author2);
                            favoriteMoviesData[i].author3 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.author3);
                            favoriteMoviesData[i].trailerUrl1 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl1);
                            favoriteMoviesData[i].trailerUrl2 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl2);
                            favoriteMoviesData[i].trailerUrl3 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.trailerUrl3);
                            favoriteMoviesData[i].review1 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.review1);
                            favoriteMoviesData[i].review2 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.review2);
                            favoriteMoviesData[i].review3 = cp.getAsString(FavoriteMovieContract.FavoriteMovieEntry.review3);
                            i++;
                        }
                        mAdapter.setData(favoriteMoviesData);

                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        errorTextView.setText("No Favorite Movies Stored");
                        errorTextView.setVisibility(View.VISIBLE);

                    }
                    super.deliverResult(favMovieData);
                }
            };
        }

            else if(id==LOADER_ID_DELETE_ALL_MOVIES){

                return new AsyncTaskLoader<Cursor>(this) {


                    @Override
                    protected void onStartLoading() {
                    }


                    @Override
                    public Cursor loadInBackground() {

                        int deletedrows= getContentResolver().delete(
                                FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,
                               null,
                                null);
                        if(deletedrows>=1)
                        {
                            Toast.makeText(getBaseContext(),"Removed All movies from favorites",Toast.LENGTH_SHORT).show();
                        }else if(deletedrows==0){
                            Toast.makeText(getBaseContext(),"No Movies Deleted"+deletedrows,Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }

                };
        }
        else{
            Toast.makeText(this,"illegal operation"+id,Toast.LENGTH_LONG).show();
            return  null;
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
    loader.stopLoading();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}


