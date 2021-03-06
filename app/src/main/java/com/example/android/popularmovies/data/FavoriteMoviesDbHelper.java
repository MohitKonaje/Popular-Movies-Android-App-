package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteMoviesDb.db";
    private static final int DATABASE_VERSION = 1;

    //if update occurs to nex version the developer would edit the following sql query string to alter the existing user database
    private static final String DATABASE_UPDATE_2 = "ALTER TABLE "
            + FavoriteMovieContract.FavoriteMovieEntry.tableName + " ADD COLUMN " + "NEW_COLUMN_NAME" + " TEXT NOT NULL;";


    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + FavoriteMovieContract.FavoriteMovieEntry.tableName + "("
                + FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY,"

                + FavoriteMovieContract.FavoriteMovieEntry.movieId + " TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.image + " TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.movieTitle + " TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.voteAverage + " TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.releaseDate + " TEXT NOT NULL,"
                + FavoriteMovieContract.FavoriteMovieEntry.movieDescription + " TEXT NOT NULL,"

                + FavoriteMovieContract.FavoriteMovieEntry.author1 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.author2 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.author3 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.trailerUrl1 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.trailerUrl2 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.trailerUrl3 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.review1 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.review2 + " TEXT,"
                + FavoriteMovieContract.FavoriteMovieEntry.review3 + " TEXT" +
                ");";


        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //if the existing/old version is older than the version required for the following update then execute following update
        if (oldVersion < 2) {
            db.execSQL(DATABASE_UPDATE_2);
        }
    }
}
