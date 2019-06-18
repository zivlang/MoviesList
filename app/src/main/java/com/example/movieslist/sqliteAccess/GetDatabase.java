package com.example.movieslist.sqliteAccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.movieslist.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.movieslist.model.Movie.TABLE_NAME;

public class GetDatabase {

    private static final int DB_VERSION = 1; //required for the constructor
    public static final String DEBUG_TAG = "MoviesList";
    private static final String dbName = "moviesList";

    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase db ;

    public GetDatabase(Context context) {
        Log.d("GetDatabase", "Cont.");

        this.sqLiteOpenHelper = new SQLiteHelper(context, dbName, null, DB_VERSION);
    }

    public GetDatabase(Context context, String scanString) {
        Log.d("GetDatabase", "Cont.");

        this.sqLiteOpenHelper = new SQLiteHelper(context, dbName, null, DB_VERSION, scanString);
//        getMovies();
    }

    public void open() {
       db = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close() {
        if (sqLiteOpenHelper != null) {
            sqLiteOpenHelper.close();
        }
    }

    public ArrayList<Movie> getMovies() {
        String[] columns = {
                Movie.ID,
                Movie.TITLE,
                Movie.IMAGE_URL,
                Movie.RATING,
                Movie.RELEASE_YEAR,
                Movie.GENRE
        };

        // sorting orders
        String sortOrder =
                Movie.RELEASE_YEAR + " ASC";
        ArrayList<Movie> moviesList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, //Table to query
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()) {

            do {
                Movie movie = new Movie();
                movie.setMovieId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Movie.ID))));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(Movie.TITLE)));
                movie.setImageUrl(cursor.getString(cursor.getColumnIndex(Movie.IMAGE_URL)));
                movie.setRating(cursor.getDouble(cursor.getColumnIndex(Movie.RATING)));
                movie.setReleaseYear(cursor.getInt(cursor.getColumnIndex(Movie.RELEASE_YEAR)));
                movie.setGenre(Collections.singletonList(String.valueOf(getGenreArray(cursor))));
//                 Adding a movie to the list
                moviesList.add(movie);
            } while (cursor.moveToNext());
        }
//        Log.d(TAG, "The movies list from sqlite: " + moviesList);
        cursor.close();
        db.close();

        return moviesList;
    }

    private Cursor getData(int id) {
        return db.rawQuery( "select * from " + TABLE_NAME + " where id="+id+"", null );
    }

    public Cursor getMovieDetails(int movieId) {

        Cursor cursor = getData(movieId);

        cursor.moveToFirst();

        return cursor;
    }

    private List<String> getGenreArray(Cursor cursor) {
        return Arrays.asList((cursor.getString(cursor.getColumnIndex(Movie.GENRE))).split(",",0));
    }
}