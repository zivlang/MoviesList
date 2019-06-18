package com.example.movieslist.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Movie implements Parcelable {

    public static final String TABLE_NAME = "movies";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String IMAGE_URL = "imageUrl";
    public static final String RATING = "rating";
    public static final String RELEASE_YEAR = "releaseYear";
    public static final String GENRE = "genre";

    private int id;
    private String title;
    private static String imageUrl;
    private Double rating;
    private int releaseYear;
    private List genre;

    public Movie() { }

    public void setMovieId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setImageUrl(String image) {
        imageUrl = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setGenre(List<String> genre) {
        this.genre = Collections.singletonList(genre);
    }

    public List<String> getGenre() {
        return genre;
    }

    @NotNull
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", rating=" + rating + '\'' +
                ", releaseYear=" + releaseYear + '\'' +
                ", genre=" + genre +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeDouble(rating);
        dest.writeInt(releaseYear);
        dest.writeString(String.valueOf(genre));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel pc){
        id = pc.readInt();
        title = pc.readString();
        imageUrl = pc.readString();
        rating = pc.readDouble();
        releaseYear = pc.readInt();
        genre = Collections.singletonList(pc.readString());
    }
}