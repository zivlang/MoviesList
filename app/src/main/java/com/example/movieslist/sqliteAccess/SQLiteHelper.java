package com.example.movieslist.sqliteAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.movieslist.fragments.MoviesListFragment;
import com.example.movieslist.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1; //required for the constructor
    private static final String TABLE_NAME = "movies";

    private String fileName = "movies.json";
    private String appName = "MoviesList";
    private String path = getExternalStorageDirectory() + "/" + appName + "/" + fileName;
    private String jsonString = null;
    public static boolean isAScannedMovie;

    private Context context;

    SQLiteHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    SQLiteHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version, String scanString) {
        super(context, DB_NAME, null, DB_VERSION);
        this.jsonString = scanString;
        this.context = context;
        SQLiteDatabase db = getWritableDatabase();
        isAScannedMovie = true;
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "At SQLiteHelper");

        createSQLIteTable(db);
        try {
            parseJsonAndInsertToSQLIte(db);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean tableIsEmpty(SQLiteDatabase db) {

        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cur.moveToFirst();
        int count = cur.getInt(0);

        if(count>0){

            Log.i(TAG,"The table is not empty");
            cur.close();
            return false;

        } else {

            Log.i(TAG,"The table is empty");
            cur.close();
            return true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void createSQLIteTable(SQLiteDatabase db) {

        //creating a table for SQLite
        String CREATE_SQL_TABLE_STRING = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
                + " ("
                + Movie.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,"
                + Movie.TITLE + " TEXT,"
                + Movie.IMAGE_URL + " TEXT,"
                + Movie.RATING + " TEXT,"
                + Movie.RELEASE_YEAR + " TEXT,"
                + Movie.GENRE + " TEXT "
                + ")";

        Log.i(TAG,"created sql table: "+CREATE_SQL_TABLE_STRING);

        db.execSQL(CREATE_SQL_TABLE_STRING);
    }

    private void parseJsonAndInsertToSQLIte(SQLiteDatabase db) throws JSONException {
        // parsing the json
        JSONArray moviesArray;
        if(tableIsEmpty(db)) {
            jsonString = getJsonFileData();
            moviesArray = new JSONArray(jsonString);
        } else {
            jsonString = MoviesListFragment.scanString;
            JSONObject jsonObject = new JSONObject(jsonString);
            moviesArray = new JSONArray();
            moviesArray.put(jsonObject);
        }

        ContentValues insertValues;

        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject jsonObject = moviesArray.getJSONObject(i);

            String title = jsonObject.getString("title");

            if(isAScannedMovie){
                if(isAlreadyOnDb(title, db) == 1){
                    break;
                }
            }

            String imageUrl = jsonObject.getString("image");
            String rating = jsonObject.getString("rating");
            String releaseYear = jsonObject.getString("releaseYear");

            JSONArray genresArray = jsonObject.getJSONArray("genre");
            List<String> genres = new ArrayList<>();
            for (int k = 0; k < genresArray.length(); k++) {
                genres.add(genresArray.getString(k));
            }

            insertValues = new ContentValues();

            insertValues.put(Movie.TITLE, title);
            insertValues.put(Movie.IMAGE_URL, imageUrl);
            insertValues.put(Movie.RATING, rating);
            insertValues.put(Movie.RELEASE_YEAR, releaseYear);

            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < genresArray.length(); k++) {
                if (k > 0) {
                    sb.append(", ");
                }
                sb.append(genres.get(k));
            }

            insertValues.put(Movie.GENRE, sb.toString());

            long res = db.insert(TABLE_NAME, null, insertValues);

            if(isAScannedMovie) {
                Toast.makeText(context, "The movie " + "'" + title + "'" +
                        " was added to the list", Toast.LENGTH_LONG).show();
            }
        }
    }

    private int isAlreadyOnDb(String title, SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + Movie.TITLE + " = ?";
        Cursor c = db.rawQuery(query, new String[]{title});
        if (c.moveToFirst()) {
            //Record exist
            c.close();
            Toast.makeText(context,"This movie is already on the list", Toast.LENGTH_LONG).show();
            return 1;
        }
        //Record doesn't exist
        c.close();
        return 0;
    }

    private String getJsonFileData() {
        //todo: consider changing this method
        //loading the jsonString
        try {
            InputStream in = new FileInputStream(new File(path));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder output = new StringBuilder();
            while ((jsonString = reader.readLine()) != null) {
                output.append(jsonString);
            }
            System.out.println(output.toString());   //Prints the string content read from input stream

            jsonString = output.toString();
            Log.d(TAG, "the jsonString was loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return jsonString;
    }
}