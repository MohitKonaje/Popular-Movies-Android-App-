package com.example.android.popularmovies.utilities;
import android.net.Uri;

import com.example.android.popularmovies.MovieDetails;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.android.popularmovies.BuildConfig.API_KEY;

/**
 * Created by mk3s on 10/14/2017.
 */

public class NetworkUtils {

    //base url
    private static final String baseUrl =" https://api.themoviedb.org";


    // this method builds a url to get movie data
   public static URL url_builder(int sort){

       //building most popular sort url
       URL built_url = null;
       Uri link = Uri.parse(baseUrl).buildUpon()
               .path("3/movie/popular")
               .appendQueryParameter("api_key",API_KEY)
               .build();

       //build top rated sort url if top rated sort option is selected
       if(sort==1){
           link=null;
           link = Uri.parse(baseUrl).buildUpon()
                   .path("3/movie/top_rated")
                   .appendQueryParameter("api_key",API_KEY)
                   .build();

       }

        try {
            built_url = new URL(link.toString());
        }catch (MalformedURLException m){
            m.printStackTrace();
        }

        return built_url;
    }

    public static MovieDetails youtube_trailer_url_builder(String[] sources,MovieDetails movie){
    String[] trailerUrls = new String[3];
        for(int i=0;i<=2;i++){
        if(sources[i]=="")
                trailerUrls[i]="";
        else
        trailerUrls[i]="https://www.youtube.com/watch?v="+sources[i];
    }
        movie.trailerUrl1=trailerUrls[0];
        movie.trailerUrl2=trailerUrls[1];
        movie.trailerUrl3=trailerUrls[2];
    return movie;
    }



    public static URL[] trailer_review_url_builder(String movie_id){
        URL[] urls = new URL[2];
        //building trailer json url
        Uri trailerLink = Uri.parse(baseUrl).buildUpon()
                .path("3/movie/"+movie_id+"/videos")
                .appendQueryParameter("api_key",API_KEY)
                .build();

        //building review json url
        Uri reviewLink = Uri.parse(baseUrl).buildUpon()
                .path("3/movie/"+movie_id+"/reviews")
                .appendQueryParameter("api_key",API_KEY)
                .build();

        try {
            urls[0] = new URL(trailerLink.toString());
            urls[1] = new URL(reviewLink.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

            return urls;

    }


          public static  String get_movie_details(URL built_url)throws IOException
    {
        //start connection
        HttpURLConnection connection = (HttpURLConnection) built_url.openConnection();

        try {
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            //get data per line
            if(scanner.hasNextLine()){
                String data="";
                while(scanner.hasNextLine()){
                 data += scanner.nextLine();
                }
            return data;
            }
            else
                return null;

        }finally {
            //end connection
            connection.disconnect();

        }
    }



        // build url for poster thumbnails in main activity
        public static String get_poster_image_url(String posterUrl){
            //initialise return url
            URL builtUrl=null;

            //build url
            Uri link = Uri.parse("http://image.tmdb.org").buildUpon()
                    .path("t/p/w342"+ posterUrl)
                    .build();
            try{
                builtUrl= new URL(link.toString());

            }catch (MalformedURLException e){
                e.printStackTrace();
            }

           return builtUrl.toString();
    }
}
