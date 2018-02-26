package com.example.android.popularmovies;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import com.example.android.popularmovies.utilities.NetworkUtils;


public class MainActivity  extends AppCompatActivity implements MovieGridAdapter.movieGridAdapterOnClickHandler {
    RecyclerView recyclerView;
    TextView errorTextView;
    ProgressBar loadBar;

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
        URL link = NetworkUtils.url_builder(selection);

        // check for internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE) ;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // start collecting data only in internet connection is available
        if(networkInfo !=null && networkInfo.isConnectedOrConnecting()){
            //make loading bar Visible
            loadBar.setVisibility(View.VISIBLE);

            //start a new Async task to fetch data
            new AsyncTaskForMovieDataCollection(this,new FetchMovieData()).execute(link);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);
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

        }
        else if (MenuItemSelection == R.id.refresh_button){
            //refresh poster layout of current sort selection;

           loadMovieData(selection);

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


}
