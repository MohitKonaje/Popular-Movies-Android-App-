package com.example.android.popularmovies;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import static com.example.android.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.movieId;


public class MainActivity  extends AppCompatActivity implements MovieGridAdapter.movieGridAdapterOnClickHandler
         {

    RecyclerView recyclerView;
    TextView errorTextView;
    ProgressBar loadBar;
    GridLayoutManager mLayout;
    //key for bundle to store the default sort selection value during activity creation/destruction
    public final String SORT_SELECTION_KEY="SELECTION_KEY";

    MovieGridAdapter mAdapter;
    int selection;


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



        //span more columns if in landscape mode
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //set layout to recyclerView
             mLayout = new GridLayoutManager(this, 3);
        }else{
            mLayout = new GridLayoutManager(this, 2);
        }

        //set fixed size
        recyclerView.setHasFixedSize(true);

        //setting grid layout to recyclerView
        recyclerView.setLayoutManager(mLayout);

        if(savedInstanceState !=null && savedInstanceState.containsKey(SORT_SELECTION_KEY))
        {
        selection= savedInstanceState.getInt(SORT_SELECTION_KEY);
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
            loadFavouriteMovieData(this);
           // getSupportLoaderManager().initLoader(LOADER_ID_All_MOVIES,null,this);
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
            recyclerView.setVisibility(View.GONE);
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
            int deletedrows= getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,
                    null,
                    null);
            if(deletedrows>0){
                Toast.makeText(this,deletedrows+" Movies deleted",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this,"No movies to be deleted",Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    //Storing data in SaveInstanceState Bundle to maintain sorting selection in Landscape and portrait mode
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_SELECTION_KEY,this.selection);

    }

    private void loadFavouriteMovieData(Context context){
    AsyncTaskLoader<Cursor> asyncTask= new AsyncTaskLoader<Cursor>(context) {
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
                return retCursor = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,
                        null,
                        null,
                        null,
                        movieId);
            } catch (Exception e) {
                Log.e("PROVIDER_ERROR", "failed to load fav movie data");
                e.printStackTrace();

                recyclerView.setVisibility(View.GONE);
                errorTextView.setText("No Favorite Movies Stored");
                errorTextView.setVisibility(View.VISIBLE);
                return null;
            }
        }

        @Override
        public void deliverResult(Cursor retCursor) {
            if ((retCursor != null) && (retCursor.getCount()>0)) {
                bindFavMovies(retCursor);
            }
            super.deliverResult(retCursor);
        }
        private void bindFavMovies(Cursor retCursor){

            MovieDetails[] favoriteMoviesData = new MovieDetails[retCursor.getCount()];
            int i = 0;
            //store cursor data in MovieData format
            retCursor.moveToFirst();
            while (i<retCursor.getCount()) {

                String title = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieTitle));
                String releaseDate  = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.releaseDate));
                String voteAverage = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.voteAverage));
                String description  = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieDescription));
                String image  = retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.image));
               String  id= retCursor.getString(retCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.movieId));

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
               favoriteMoviesData[i] = new MovieDetails(title,releaseDate,voteAverage,description,image,id,
                       trailerUrl1,trailerUrl2,trailerUrl13,author1,author2,author3,review1,review2,review3);

                i++;
                retCursor.moveToNext();
            }
            //handle loadingBar
            recyclerView.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.INVISIBLE);
            mAdapter.setData(favoriteMoviesData);
        }
    };
   asyncTask.startLoading();
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





}


