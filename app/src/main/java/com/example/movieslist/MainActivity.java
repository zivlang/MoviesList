package com.example.movieslist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.movieslist.fragments.MoviesListFragment;
import com.example.movieslist.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Monitoring", "At MainActivity's onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Movie> moviesList = this.getIntent().getParcelableArrayListExtra("moviesList");
        Bundle listBundle = new Bundle();
        listBundle.putParcelableArrayList("moviesList", moviesList);

        Fragment mlf = new MoviesListFragment();
        mlf.setArguments(listBundle);
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragments_container, mlf);

        Log.d("Monitoring", "Going to MoviesListFragment");

        ft.commit();
    }
}
