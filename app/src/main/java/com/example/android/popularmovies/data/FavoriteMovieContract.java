package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by mk3s on 16-Mar-18.
 */

public class FavoriteMovieContract {
    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String MOVIES_PATH = "FavMovies";


    public static final class FavoriteMovieEntry implements BaseColumns {
        public static final  Uri FAVROITE_MOVIES_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

        public static final String tableName = "FavMovies";
        public static final String movieId = "MovieId";
        public static final String image = "Image";
        public static final String movieTitle= "Title";
        public static final String releaseDate = "ReleaseDate";
        public static final String voteAverage = "VoteAverage";
        public static final String movieDescription = "Description";


        public static final String trailerUrl1 = "TrailerUrl1";
        public static final String trailerUrl2 ="TrailerUrl2";
        public static final String trailerUrl3 = "TrailerUrl3";
        public static final String author1 = "Author1";
        public static final String author2 = "Author2";
        public static final String author3 = "Author3";
        public static final String review1 = "Review1";
        public static final String review2 = "Review2";
        public static final String review3 = "Review3";



    }
}
