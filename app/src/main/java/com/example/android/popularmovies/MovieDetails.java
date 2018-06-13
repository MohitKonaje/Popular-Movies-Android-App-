package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

//create data object and implement parcelable
public class MovieDetails implements Parcelable {
   public String title, releaseDate, voteAverage, description,image,movieId,
           trailerUrl1,trailerUrl2,trailerUrl3,author1,author2,author3,
           review1, review2, review3;


   public MovieDetails(String passedTitle, String passedReleaseDate, String passedVoteAverage, String passedDescription, String passedImageUrl
   ,String passedMovieId,String passedTrailerUrl1,String passedTrailerUrl2,String passedTrailerUrl3,String passedAuthor1,String passedAuthor2,
                       String passedAuthor3,String passedReview1,String passedReview2,String passedReview3)
    {
        this.title =passedTitle;
        this.releaseDate =passedReleaseDate;
        this.voteAverage =passedVoteAverage;
        this.description =passedDescription;
        this.image= passedImageUrl;

        this.movieId= passedMovieId;
        this.trailerUrl1 =passedTrailerUrl1;
        this.trailerUrl2=passedTrailerUrl2;
        this.trailerUrl3=passedTrailerUrl3;
        this.author1=passedAuthor1;
        this.author2=passedAuthor2;
        this.author3=passedAuthor3;
        this.review1 =passedReview1;
        this.review2 =passedReview2;
        this.review3 =passedReview3;
    }

    private MovieDetails(Parcel p){
        title =p.readString();
        releaseDate =p.readString();
        voteAverage =p.readString();
        description =p.readString();
        image=p.readString();

        movieId=p.readString();
        trailerUrl1 =p.readString();
        trailerUrl2 =p.readString();
        trailerUrl3 =p.readString();
        author1=p.readString();
        author2=p.readString();
        author3=p.readString();
        review1 =p.readString();
        review2 =p.readString();
        review3 =p.readString();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(releaseDate);
        out.writeString(voteAverage);
        out.writeString(description);
        out.writeString(image);

        out.writeString(movieId);
        out.writeString(trailerUrl1);
        out.writeString(trailerUrl2);
        out.writeString(trailerUrl3);
        out.writeString(review1);
        out.writeString(review2);
        out.writeString(review3);

    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>(){

        @Override
        public MovieDetails createFromParcel(Parcel parcel) {
            return new MovieDetails(parcel);
        }

        @Override
        public MovieDetails[] newArray(int i) {
            return new MovieDetails[i];
        }
    };
}
