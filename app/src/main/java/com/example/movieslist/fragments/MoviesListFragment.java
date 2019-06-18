package com.example.movieslist.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.movieslist.R;
import com.example.movieslist.SplashActivity;
import com.example.movieslist.adapterAndDivider.MoviesAdapter;
import com.example.movieslist.model.Movie;
import com.example.movieslist.sqliteAccess.GetDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.movieslist.sqliteAccess.SQLiteHelper.isAScannedMovie;

public class MoviesListFragment extends Fragment {

    Context context;
    ArrayList<Movie> moviesList;

    MoviesAdapter moviesAdapter;
    RecyclerView moviesRV;

    Button scanAMovieBarcode;
    //for creating a scanner object
    IntentIntegrator intentIntegrator;

    public static String scanString;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_movies, container, false);

        Log.i(TAG,"In moviesListFragment");

        context = getActivity();

        scanAMovieBarcode = rootView.findViewById(R.id.addBtnId);

        intentIntegrator = new IntentIntegrator(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        moviesAdapter = new MoviesAdapter(context);

        moviesRV = rootView.findViewById(R.id.moviesRVId);
        moviesRV.setLayoutManager(linearLayoutManager);
        moviesRV.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(context, linearLayoutManager.getOrientation());

        moviesRV.addItemDecoration(dividerItemDecoration);
        moviesRV.setAdapter(moviesAdapter);

        Bundle listBundle = getArguments();
        if (listBundle != null) {
            moviesList = getArguments().getParcelableArrayList("moviesList");
            moviesAdapter.attachMoviesList(moviesList);
        }

        scanAMovieBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(MoviesListFragment.this).initiateScan();
            }
        });
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            scanString = result.getContents();
            new GetDatabase(getActivity(),scanString);

            if(isAScannedMovie) {
                isAScannedMovie = false;
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.putExtra("isCalledByIntent", true);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}