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
import android.os.Handler;
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

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

public class SplashActivity extends Activity {

    AsyncTask<String, String, String> downloadOrGetFile;

    boolean isCalledByIntent, fileExists;
    private boolean permissionResult;

    private String fileName = "movies.json";
    private String appName = "MoviesList";
    private String path = getExternalStorageDirectory() + "/" + appName + "/" + fileName;

    @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            if(!isCalledByIntent) {
                setContentView(R.layout.activity_splash);
            }

            File file = new File(path);
            fileExists = file.exists();
            if (fileExists) {

                Log.d(TAG, path + " exists");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadOrGetFile = new DownloadOrGetFile().execute();
                    }
                }, 2000);

            } else {
                if (!checkPermission()) {
                    downloadOrGetFile = new DownloadOrGetFile().execute();
                } else {
                    if (checkPermission()) {
                        requestPermissionAndContinue();
                    }
                }
            }
        }

    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissionResult = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        }
        return permissionResult;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_necessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        } else {
            downloadOrGetFile = new DownloadOrGetFile().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    downloadOrGetFile = new DownloadOrGetFile().execute();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public class DownloadOrGetFile extends AsyncTask<String,String,String> {
        ArrayList<Movie> moviesList;

        GetDatabase getDatabase = new GetDatabase(SplashActivity.this);

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            if(!fileExists){
                download();
            }
            moviesList = getListFromDatabase();
            toMainActivity(moviesList);

            return null;
        }

        private void download() {
            try {
                //connecting the web page from which data will be read
                String downloadUrl = "https://api.androidhive.info/json/movies.json";
                HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(downloadUrl).openConnection();
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
                FileOutputStream fos;
                String folder = fileFolderDirectory();
                path = folder + fileName;
                try {
                    fos = new FileOutputStream(new File(path));
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
                boolean directoryExists = directory.mkdirs();
                Log.i("fileFolderDirectory", String.valueOf(directoryExists));
            }
            return folder;
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