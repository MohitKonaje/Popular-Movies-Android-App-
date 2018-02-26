package com.example.android.popularmovies.utilities;


import com.example.android.popularmovies.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;


/**
 * Created by mk3s on 22-Oct-17.
 */

public class MovieJsonUtils {

    public static MovieDetails[] get_values(String jsonString){
        MovieDetails[] movie_values=null;
        try{
            //creating complete json result
        JSONObject object = new JSONObject(jsonString);

            //filtering results(selecting data only in results object)
            JSONArray results = object.getJSONArray("results");

            //initialising movie_values to size of result
            movie_values= new MovieDetails[results.length()];

            for(int i=0;i< results.length();i++){
                //select a movie
            JSONObject movie = results.getJSONObject(i);
                //extract title
                String title=movie.getString("title");
                //extract release date
                String releaseDate =movie.getString("release_date");
                //extract voter average
                String voterAverage =movie.getString("vote_average");
                //extract description
                String description=movie.getString("overview");
                //extract poster url string
                String poster_path=movie.getString("poster_path");
                //build and store poster url
                String posterUrl = NetworkUtils.get_poster_image_url(poster_path);
                //extract movie id string
                String movieId=movie.getString("id");


                movie_values[i]=new MovieDetails(title, releaseDate, voterAverage,description, posterUrl,movieId,
                        null,null,null,null,null,null);
            }


    }catch (Exception exception){
            exception.printStackTrace();
        }
        return movie_values;
    }

    public static MovieDetails get_reviews_values(String jsonString, MovieDetails data){
try {

    //create json object to read json data
    JSONObject object = new JSONObject(jsonString);
    //select results
    JSONArray results = object.getJSONArray("results");
    //create string to store reviews
    String [] reviews = new String[2];
    //collect reviews
    for(int i=0;i< 2;i++){
        JSONObject reviewItem = results.getJSONObject(i);
        reviews[i] = reviewItem.getString("content");
    }

    //store reviews which are available

    if(reviews[0]==null) {
        data.reviewUrl1 = "No reviews in this movie";
        data.reviewUrl2 = "";
        data.reviewUrl3 = "";
        return data;
    }
        else
        data.reviewUrl1=reviews[0];

    if(reviews[1]==null) {
        data.reviewUrl2 = "";
        data.reviewUrl3 = "";
        return data;
    }
        else
        data.reviewUrl2=reviews[1];

    if(reviews[2]==null) {
        data.reviewUrl3 = "";
        return data;
    }
        else
        data.reviewUrl3=reviews[2];


}catch (JSONException e){
    e.printStackTrace();
}
    return data;
    }


    public static String[] get_trailer_values(String jsonString){
        //create string to store trailer urls
        String [] trailersPaths = new String[2];
        try{
        JSONObject object = new JSONObject(jsonString);
            JSONArray results = object.getJSONArray("youtube");
            //collect reviews
            for(int i=0;i< 2;i++){
                JSONObject trailerItem = results.getJSONObject(i);
                trailersPaths[i] = trailerItem.getString("source");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return trailersPaths;
    }

}
