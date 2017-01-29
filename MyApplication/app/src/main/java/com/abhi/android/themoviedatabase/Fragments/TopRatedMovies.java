package com.abhi.android.themoviedatabase.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhi.android.themoviedatabase.Adapter.MovieViewAdapter;
import com.abhi.android.themoviedatabase.Model.Movie;
import com.abhi.android.themoviedatabase.R;
import com.abhi.android.themoviedatabase.Utils.TinyUrls;
import com.abhi.android.themoviedatabase.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by abhi on 1/28/17.
 */

public class TopRatedMovies extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private ArrayList<Movie> mMovieList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 4;
    private int pageCount = 1;


    private static final int TOP_MOVIE_LOADER = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView)  inflater.inflate(R.layout.content_main, container, false);

        mMovieList = new ArrayList<>();
        mAdapter = new MovieViewAdapter(getActivity(), mMovieList);

        mGridLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        String url = TinyUrls.top_rated_movies + Utils.API_KEY;

        executeLoader("movies", url,TOP_MOVIE_LOADER);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mGridLayoutManager.getItemCount();
                firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        pageCount++;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    String url = TinyUrls.top_rated_movies +Utils.API_KEY + "&page=" + String.valueOf(pageCount);
                    executeLoader("movies", url, TOP_MOVIE_LOADER);
                    loading = true;
                }

            }
        });


        return mRecyclerView;


    }


    private void executeLoader(String item, String url, int id){
        Bundle queryBundle= new Bundle();
        queryBundle.putString(item,url);


        LoaderManager loaderManager = getLoaderManager();
        Loader<String> trailerLoader = loaderManager.getLoader(id);

        if(trailerLoader== null){
            loaderManager.initLoader(id,queryBundle,this);
        }else{
            loaderManager.restartLoader(id,queryBundle,this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(getContext()) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){
                    return;
                }
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String url = args.getString("movies");

                if ( url == null || url.isEmpty())
                    return null;
                try {
                    return Utils.connectToInternet(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray arrayResult= jsonObject.getJSONArray("results");

                for(int i = 0; i < arrayResult.length(); i++){
                    JSONObject movie = arrayResult.getJSONObject(i);
                    String movie_name = movie.getString("original_title");
                    int movie_id = movie.getInt("id");
                    String movie_poster_path = TinyUrls.image_path + movie.getString("poster_path");
                    String movie_plot = movie.getString("overview");
                    String movie_rating = movie.getString("vote_average");
                    String movie_release_date = movie.getString("release_date");

                    Movie movie1 = new Movie(movie_name,movie_poster_path,movie_plot,movie_rating,movie_release_date,movie_id, movie);

                    mMovieList.add(movie1);
                }

                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
