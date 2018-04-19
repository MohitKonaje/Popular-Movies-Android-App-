package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by mk3s on 14-Mar-18.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteMoviesDb.db";
    private static final int DATABASE_VERSION = 1;


    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable="CREATE TABLE " +FavoriteMovieContract.FavoriteMovieEntry.tableName+"("
                + FavoriteMovieContract.FavoriteMovieEntry._ID +" INTEGER PRIMARY KEY,"

                + FavoriteMovieContract.FavoriteMovieEntry.movieId +" TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.image +" TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.movieTitle +" TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.voteAverage +" TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.releaseDate +" TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.movieDescription +" TEXT NOT NULL,"

                + FavoriteMovieContract.FavoriteMovieEntry.author1 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.author2 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.author3 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.trailerUrl1 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.trailerUrl2 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.trailerUrl3 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.review1 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.review2 +" TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.review3 +" TEXT," +
                ");";


        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavoriteMovieEntry.tableName);
        onCreate(db);
    }
}
