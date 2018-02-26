package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mk3s on 10/12/2017.
 */
//crate data object and implement parcelable
public class MovieDetails implements Parcelable {
   public String title, releaseDate, voteAverage, description,image,movieId,
           trailerUrl1,trailerUrl2,trailerUrl3,
           reviewUrl1,reviewUrl2,reviewUrl3;


   public MovieDetails(String passedTitle, String passedReleaseDate, String passedVoteAverage, String passedDescription, String passedImageUrl
   ,String passedMovieId,String passedTrailerUrl1,String passedTrailerUrl2,String passedTrailerUrl3,String passedReviewUrl1,String passedReviewUrl2,String passedReviewUrl3)
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
        this.reviewUrl1=passedReviewUrl1;
        this.reviewUrl2=passedReviewUrl2;
        this.reviewUrl3=passedReviewUrl3;
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
        reviewUrl1 =p.readString();
        reviewUrl2 =p.readString();
        reviewUrl3 =p.readString();

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
        out.writeString(reviewUrl1);
        out.writeString(reviewUrl2);
        out.writeString(reviewUrl3);

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
