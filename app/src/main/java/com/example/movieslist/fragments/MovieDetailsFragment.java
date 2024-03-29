package com.example.movieslist.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.movieslist.R;
import com.example.movieslist.model.Movie;
import com.example.movieslist.sqliteAccess.GetDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsFragment extends Fragment{

    List<String> genres = new ArrayList<>();

    int movieId;

    GetDatabase getDatabase;

    ImageView movieImageView;

    TextView titleView;
    TextView ratingView;
    TextView releaseYearView;
    TextView genreView;

    ProgressBar pb;

    static boolean couldNotLoadImage;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details_movie, container, false);

        Context context = getActivity();

        Bundle idBundle = getArguments();
        if (idBundle != null) {
            movieId = getArguments().getInt("id");
        }

        getDatabase = new GetDatabase(context);
        getDatabase.open();
        Cursor cursor = getDatabase.getMovieDetails(movieId);

        movieImageView = rootView.findViewById(R.id.movieImageId);
        pb = rootView.findViewById(R.id.progress);
        String imageURLFromSQLite = cursor.getString(cursor.getColumnIndex(Movie.IMAGE_URL));

        if(!couldNotLoadImage) {
            Glide.with(MovieDetailsFragment.this)
                    .load(imageURLFromSQLite)
                    .apply(new RequestOptions().override(300, 300))
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            pb.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Error: image URL issue", Toast.LENGTH_LONG).show();
                            couldNotLoadImage = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pb.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(movieImageView);
        }
        else{
            pb.setVisibility(View.GONE);
        }
        titleView = rootView.findViewById(R.id.movieTtlId);
        ratingView = rootView.findViewById(R.id.ratingId);
        releaseYearView = rootView.findViewById(R.id.releaseYearId);
        genreView = rootView.findViewById(R.id.genreId);

        String titleFromSQLite = cursor.getString(cursor.getColumnIndex(Movie.TITLE));
        String ratingFromSQLite = cursor.getString(cursor.getColumnIndex(Movie.RATING));
        String releaseYearFromSQLite = cursor.getString(cursor.getColumnIndex(Movie.RELEASE_YEAR));

        String genreFromSQLite;
        if(cursor.moveToFirst()) {
            do {
                genreFromSQLite = cursor.getString(cursor.getColumnIndex(Movie.GENRE));
                genres.add(genreFromSQLite);
            } while (cursor.moveToNext());
        }
        else{
            genreFromSQLite = cursor.getString(cursor.getColumnIndex(Movie.RELEASE_YEAR));
        }
        getDatabase.close();

        titleView.setText(titleFromSQLite);
        titleView.setFocusable(false);
        titleView.setClickable(false);

        ratingView.setText(ratingFromSQLite);
        ratingView.setFocusable(false);
        ratingView.setClickable(false);

        releaseYearView.setText(releaseYearFromSQLite);
        releaseYearView.setFocusable(false);
        releaseYearView.setClickable(false);

        genreView.setText(genreFromSQLite);
        genreView.setFocusable(false);
        genreView.setClickable(false);

        return rootView;
    }
}