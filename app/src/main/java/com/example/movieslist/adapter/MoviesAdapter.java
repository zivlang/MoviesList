package com.example.movieslist.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.movieslist.R;
import com.example.movieslist.fragments.MovieDetailsFragment;
import com.example.movieslist.model.Movie;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.RowViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public MoviesAdapter(Context context) {

        this.context = context;
        movieList = new ArrayList<>();
    }

    @NotNull
    @Override
    public RowViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.row_list_movies, parent,false);

        return new RowViewHolder(itemView);
    }

    class RowViewHolder extends RecyclerView.ViewHolder {

        TextView viewTitle;
        TextView viewReleaseYear;

        LinearLayout movieEntry;

        RowViewHolder(View view) {

            super(view);
            viewTitle = view.findViewById(R.id.title);
            viewReleaseYear = view.findViewById(R.id.releaseYear);
            movieEntry = view.findViewById(R.id.eventsListRowId);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RowViewHolder holder, int position) {

        final Movie currentMovie = movieList.get(position);

        holder.viewTitle.setText(movieList.get(position).getTitle());
        holder.viewReleaseYear.setText(String.valueOf(movieList.get(position).getReleaseYear()));
        holder.movieEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toMovieDetailsFragment(currentMovie, v);
            }
        });
    }

    private void toMovieDetailsFragment(Movie currentMovie, View v) {

        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        int id = currentMovie.getMovieId();

        Fragment mdf = new MovieDetailsFragment();

        Bundle movieBundle = new Bundle();
        movieBundle.putInt("id", id);
        Log.d("Monitoring", "Clicked id: "+ id);
        mdf.setArguments(movieBundle);

        Log.d("Monitoring", "Going to MovieDetailsFragment");

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, mdf)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void attachMoviesList(List<Movie> movieList) {
        // getting list from Fragment.
        this.movieList = movieList;
    }
}