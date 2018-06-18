package com.example.android.popularmovies.utilities;


import com.example.android.popularmovies.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public final class MovieJsonUtils {
    //constants for json values
    private static final String RESULTS_JSON_OBJECT = "results";
    private static final String TITLE_JSON_KEY = "title";
    private static final String RELEASE_DATE_JSON_KEY = "release_date";
    private static final String VOTE_AVERAGE_JSON_KEY = "vote_average";
    private static final String OVERVIEW_JSON_KEY = "overview";
    private static final String POSTER_PATH_JSON_KEY = "poster_path";
    private static final String ID_JSON_KEY = "id";
    private static final String AUTHOR_JSON_KEY = "author";
    private static final String CONTENT_JSON_KEY = "content";
    private static final String KEY_JSON_KEY = "key";

    private static final String NO_REVIEWS_MESSAGE = "No reviews in this movie";

    //json utils will not be instantiated only its method will be used
    private MovieJsonUtils() {
    }

    public static MovieDetails[] get_values(String jsonString) {
        MovieDetails[] movie_values = null;
        try {
            //creating complete json result
            final JSONObject object = new JSONObject(jsonString);

            //filtering results(selecting data only in results object)
            final JSONArray RESULTS = object.getJSONArray(RESULTS_JSON_OBJECT);

            //initialising movie_values to size of result
            movie_values = new MovieDetails[RESULTS.length()];

            for (int i = 0; i < RESULTS.length(); i++) {
                //select a movie
                JSONObject movie = RESULTS.getJSONObject(i);
                //extract title
                String title = movie.getString(TITLE_JSON_KEY);
                //extract release date
                String releaseDate = movie.getString(RELEASE_DATE_JSON_KEY);
                //extract voter average
                String voterAverage = movie.getString(VOTE_AVERAGE_JSON_KEY);
                //extract description
                String description = movie.getString(OVERVIEW_JSON_KEY);
                //extract poster url string
                String poster_path = movie.getString(POSTER_PATH_JSON_KEY);
                //build and store poster url
                String posterUrl = NetworkUtils.get_poster_image_url(poster_path);
                //extract movie id string
                String movieId = movie.getString(ID_JSON_KEY);


                movie_values[i] = new MovieDetails(title, releaseDate, voterAverage, description, posterUrl, movieId,
                        null, null, null,
                        null, null, null, null, null, null);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return movie_values;
    }

    public static MovieDetails get_reviews_values(String jsonString, MovieDetails selectedMovie) {
        try {
            //create json object to read json data
            final JSONObject object = new JSONObject(jsonString);
            //select results
            JSONArray results = object.getJSONArray(RESULTS_JSON_OBJECT);
            //create string array to store reviews
            String[] reviews = new String[3];
            //create string array to store reviews
            String[] authors = new String[3];
            //collect reviews
            for (int i = 0; i <= 2; i++) {
                if (i > results.length() - 1) {
                    authors[i] = "";
                    reviews[i] = "";
                } else {
                    JSONObject reviewItem = results.getJSONObject(i);
                    authors[i] = reviewItem.getString(AUTHOR_JSON_KEY);
                    reviews[i] = reviewItem.getString(CONTENT_JSON_KEY);
                }
            }
            //update the movie data with reviews and authors

            if (reviews[0].equals("")) {
                selectedMovie.review1 = NO_REVIEWS_MESSAGE;
            } else {
                selectedMovie.review1 = reviews[0];
            }
            selectedMovie.review2 = reviews[1];
            selectedMovie.review3 = reviews[2];
            selectedMovie.author1 = authors[0];
            selectedMovie.author2 = authors[1];
            selectedMovie.author3 = authors[2];

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return selectedMovie;
    }

    public static String[] get_trailer_values(String jsonString) {
        //create string to store trailer urls
        String[] trailersPaths = new String[3];
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray results = object.getJSONArray(RESULTS_JSON_OBJECT);
            //collect reviews
            for (int i = 0; i <= 2; i++) {
                if (i > results.length() - 1) {
                    trailersPaths[i] = "";
                } else {
                    JSONObject trailerItem = results.getJSONObject(i);
                    if (trailerItem == null)
                        trailersPaths[i] = "";
                    else
                        trailersPaths[i] = trailerItem.getString(KEY_JSON_KEY);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailersPaths;
    }
}
