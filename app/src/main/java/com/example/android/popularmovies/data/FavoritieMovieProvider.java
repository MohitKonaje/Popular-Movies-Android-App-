package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by mk3s on 13-Mar-18.
 */

public class FavoritieMovieProvider extends ContentProvider {


   private FavoriteMoviesDbHelper dbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher (){
        UriMatcher builder = new UriMatcher(UriMatcher.NO_MATCH);
        builder.addURI(FavoriteMovieContract.AUTHORITY,FavoriteMovieContract.MOVIES_PATH,MOVIES);
        builder.addURI(FavoriteMovieContract.AUTHORITY,FavoriteMovieContract.MOVIES_PATH + "#",MOVIE_WITH_ID);
        return builder;
    }



    @Override
    public boolean onCreate() {
        Context context = getContext();
    dbHelper = new FavoriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selections, @Nullable String[] selectionsArgs,
                        @Nullable String sortOrder) {
        Cursor retCursor;
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIE_WITH_ID:
                retCursor=db.query(FavoriteMovieContract.FavoriteMovieEntry.tableName,projections,selections,selectionsArgs,null,null
                ,sortOrder);
                break;
            default:
                throw new android.database.SQLException ("No query executed"+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);


        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
      int match = sUriMatcher.match(uri);
        Uri retUri;
        switch(match){

            case MOVIES:
                long id=db.insert(FavoriteMovieContract.MOVIES_PATH,null,contentValues);

                if(id>0){
                retUri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.FAVROITE_MOVIES_CONTENT_URI,id);

                }else{
                    throw new android.database.SQLException("No records entered" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

    return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArguments) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows;
        int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIE_WITH_ID:
                deletedRows=db.delete(FavoriteMovieContract.FavoriteMovieEntry.tableName,whereClause,whereArguments);
                if(deletedRows!=1){
                    throw new UnsupportedOperationException("Unknown uri:"+ uri);
                }
                break;
            case MOVIES:
                     deletedRows=db.delete(FavoriteMovieContract.FavoriteMovieEntry.tableName,null,null);
                break;
        default:
            throw new UnsupportedOperationException("Invalid uri:"+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
