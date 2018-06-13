package com.example.android.popularmovies.utilities;


import com.example.android.popularmovies.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




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
                        null,null,null,
                        null,null,null,null,null,null);
            }


    }catch (Exception exception){
            exception.printStackTrace();
        }
        return movie_values;
    }

    public static MovieDetails get_reviews_values(String jsonString, MovieDetails selectedMovie){
try {

    //create json object to read json data
    JSONObject object = new JSONObject(jsonString);
    //select results
    JSONArray results = object.getJSONArray("results");
    //create string array to store reviews
    String [] reviews = new String[3];
    //create string array to store reviews
    String [] authors = new String[3];
    //collect reviews
    for(int i=0;i<= 2;i++){
        if(i>results.length()-1){
            authors[i]="";
            reviews[i]="";
        }
        else{
        JSONObject reviewItem = results.getJSONObject(i);
        authors[i] = reviewItem.getString("author");
        reviews[i] = reviewItem.getString("content");
    }}

    //update the movie data with reviews and authors

    if(reviews[0].equals("")) {
        selectedMovie.review1 = "No reviews in this movie";
    }else{
        selectedMovie.review1= reviews[0];
    }
    selectedMovie.review2=reviews[1];
    selectedMovie.review3=reviews[2];
    selectedMovie.author1=authors[0];
    selectedMovie.author2=authors[1];
    selectedMovie.author3=authors[2];

}catch (JSONException e){
    e.printStackTrace();
}
    return selectedMovie;
    }


    public static String[] get_trailer_values(String jsonString){
        //create string to store trailer urls
        String [] trailersPaths = new String[3];
        try{
        JSONObject object = new JSONObject(jsonString);
            JSONArray results = object.getJSONArray("results");
            //collect reviews
            for(int i=0;i<= 2;i++) {
                if (i > results.length() - 1) {
                    trailersPaths[i] = "";
                } else {
                    JSONObject trailerItem = results.getJSONObject(i);
                    if (trailerItem == null)
                        trailersPaths[i] = "";
                    else
                        trailersPaths[i] = trailerItem.getString("key");
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return trailersPaths;
    }

}
