package com.example.movieslist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.movieslist.model.Movie;
import com.example.movieslist.sqliteAccess.GetDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

public class SplashActivity extends Activity {

//    SplashActivity splashActivity;
//
//    Context context;

//    String fileName = "movies.json";
//    static String appName = "MoviesList";
//    String path = getExternalStorageDirectory() + "/" + appName + "/" + fileName;
//    ArrayList<Movie> moviesList;

    AsyncTask<String, String, String> downloadJSON;

    boolean isCalledByIntent;
//    GetDatabase getDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isCalledByIntent) {
            setContentView(R.layout.activity_splash);
        }

        downloadJSON = new DownloadJSON().execute("https://api.androidhive.info/json/movies.json");
    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            downloadJSON = new DownloadJSON().execute("https://api.androidhive.info/json/movies.json");
        }
    }
    public class DownloadJSON extends AsyncTask<String,String,String> {

        private String fileName = "movies.json";
        private String appName = "MoviesList";
        private String path = getExternalStorageDirectory() + "/" + appName + "/" + fileName;

        ArrayList<Movie> moviesList;

        GetDatabase getDatabase = new GetDatabase(SplashActivity.this);

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            File file = new File(path);
            if(file.exists()){
                Log.d(TAG, path + " exists");

                moviesList = getListFromDatabase();
                toMainActivity(moviesList);
            }

            // checking if the user has already granted the permissions
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    Log.v(TAG,"Permission is already granted");
                    Log.d(TAG, "The JSON file doesn't exists");
                    start();
                    moviesList = getListFromDatabase();
                    toMainActivity(moviesList);}

                //asking for permissions, in case  the user hasn't already granted the permissions
                else{
                    Log.d(TAG, "Asking for permissions");
                    Log.d(TAG, "The JSON file doesn't exists");

                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{

                            WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE,
                            CAMERA}, PERMISSION_REQUEST_CODE);
                }
            }
            else{
                start();
                moviesList = getListFromDatabase();
                toMainActivity(moviesList);
            }

            return null;
        }

        private void start() {
            try {
                //connecting the web page from which data will be read
                HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://api.androidhive.info/json/movies.json").openConnection();
                //an object that reads from the internet
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder fullJSON = new StringBuilder(); // a string that will hold the JSON
                String line; // will hold a certain line from the JSON
                while ((line = bufferedReader.readLine()) != null) { //unless the read line is null,
                    // it's being saved in line
                    fullJSON.append(line); // adding the read line to the already saved string
                }
                //Close our InputStream and Buffered reader
                bufferedReader.close();
                String responseTxt = fullJSON.toString();
                Log.d(TAG, "doInBackground: responseText " + responseTxt);
                // PREPARE FOR WRITING A FILE TO A DEVICE DIRECTORY
                FileOutputStream fos = null;
                String folder = fileFolderDirectory();
                path = folder + fileName;
                try {
                    fos = new FileOutputStream(new File(path));
                    //fos = openFileOutput(folder + fileName, MODE_PRIVATE);
                    fos.write(responseTxt.getBytes());
                    Log.d(TAG, "downloaded ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        private String fileFolderDirectory() {
            String folder = Environment.getExternalStorageDirectory() + "/" +appName +"/";
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            return folder;
        }

        private boolean shouldCheckPermission() {
            return ContextCompat.checkSelfPermission(SplashActivity.this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(SplashActivity.this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(SplashActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermissionAndContinue() {
            // if permission wasn't already given
            if (ContextCompat.checkSelfPermission(SplashActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(SplashActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(SplashActivity.this, CAMERA) != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, WRITE_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, READ_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, CAMERA)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SplashActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(getString(R.string.permission_necessary));
                    alertBuilder.setMessage(R.string.permission_dialog_message);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                    , READ_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    Log.i("", "permission denied, show dialog");

                } else {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
                }
            }
        }

        private ArrayList<Movie> getListFromDatabase() {

            getDatabase.open();
            moviesList = getDatabase.getMovies();
            getDatabase.close();

            return moviesList;
        }
    }

    private void toMainActivity(ArrayList<Movie> moviesList) {

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putParcelableArrayListExtra("moviesList", moviesList);

        Log.d("Monitoring", "going to main activity");

        startActivity(intent);
        finish();

        Log.i("Monitoring", "splash is over");
    }
}