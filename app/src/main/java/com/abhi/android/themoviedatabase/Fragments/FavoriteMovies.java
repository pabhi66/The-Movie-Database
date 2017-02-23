package com.abhi.android.themoviedatabase.Fragments;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.abhi.android.themoviedatabase.Activity.MainActivity;
import com.abhi.android.themoviedatabase.Adapter.MovieViewAdapter;
import com.abhi.android.themoviedatabase.Model.Movie;
import com.abhi.android.themoviedatabase.R;
import com.abhi.android.themoviedatabase.Utils.TinyUrls;
import com.abhi.android.themoviedatabase.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FavoriteMovies extends Fragment {

    public static int imageSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView mRecyclerView = (RecyclerView) inflater.inflate(R.layout.content_main, container, false);

        ArrayList<Movie> mMovieList = new ArrayList<>();

        RecyclerView.Adapter mAdapter = new MovieViewAdapter(getActivity(), mMovieList);

        GridLayoutManager mGridLayoutManager;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && Utils.screenSize >=7){
            mGridLayoutManager = new GridLayoutManager(getActivity(),6);
            imageSize = 1;
        }else if(Utils.screenSize >= 7){
            mGridLayoutManager = new GridLayoutManager(getActivity(),5);
            imageSize = 4;
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridLayoutManager = new GridLayoutManager(getActivity(),4);
            imageSize = 2;
        }
        else{
            mGridLayoutManager = new GridLayoutManager(getActivity(),2);
            imageSize = 3;
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        for(String value: MainActivity.favorite_movies.values()){
            try {
                JSONObject movie = new JSONObject(value);

                String movie_name = movie.getString("original_title");
                int movie_id = movie.getInt("id");
                String movie_poster_path = TinyUrls.image_path + movie.getString("poster_path");
                String movie_plot = movie.getString("overview");
                String movie_rating = movie.getString("vote_average");
                String movie_release_date = movie.getString("release_date");

                Movie movie1 = new Movie(movie_name,movie_poster_path,movie_plot,movie_rating,movie_release_date,movie_id, movie);

                mMovieList.add(movie1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mRecyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.getMovies();
    }
}
