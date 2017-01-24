package com.abhi.android.moviedb;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MoviesView extends Fragment {


    //views
    static GridView moviesView;
    static ProgressBar progressBar;

    static ArrayList<String> movie_posters;
    static JSONArray results;

    static ArrayList<String> movies_favorites;
    static boolean remove = false;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //get the view
        View rootView = inflater.inflate(R.layout.fragment_movies_view, container, false);
        Utils.getDeviceSize(getActivity());

        //get the views from layout and assign them
        moviesView = (GridView) rootView.findViewById(R.id.movie_posters);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        //if the device is larger then 6 inch then show 4 posters per row otherwise 2 posters per row
        if(Utils.screenSize >= 5){
            moviesView.setColumnWidth(Utils.width/4);
            moviesView.setAdapter(new ImageAdapter(getActivity(),movie_posters,Utils.width/4));
        }else{
            moviesView.setColumnWidth(Utils.width/2);
            moviesView.setAdapter(new ImageAdapter(getActivity(),movie_posters,Utils.width/2));

        }



        //when user click on the poster open a new activity with movie details
        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                if(Utils.sort_by_favorites){
                    Intent intent = new Intent(getActivity(),MovieDetail.class);
                    intent.putExtra("movie",movies_favorites.get(position));
                    startActivity(intent);
                }else {
                    try {
                        JSONObject movie = results.getJSONObject(position);
                        Intent intent = new Intent(getActivity(), MovieDetail.class);
                        intent.putExtra("movie", movie.toString());
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        //loadFavoriteMovies();
        if(movie_posters == null || remove) {
            if (Utils.isInternetAvailable(getActivity())) {

                if(remove && ! Utils.sort_by_favorites){
                    remove = false;
                    return;
                }

                //load movies
                String url;
                if (Utils.sort_by_popularity) {
                    getActivity().setTitle("Popular Movies");
                    url = "http://api.themoviedb.org/3/movie/popular?api_key=" + Utils.API_KEY;
                } else if (Utils.sort_by_ratings) {
                    getActivity().setTitle("Top Rated Moves");
                    url = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + Utils.API_KEY;
                }else if(Utils.sort_by_favorites){
                    getActivity().setTitle("My Favorite Movies");
                    ArrayList<String> JSONRequstedResult;


                    String[] posters = new String[MainActivity.favorite_movies.size()];

                    int i = 0;
                    movies_favorites = new ArrayList<>();
                    for(String value: MainActivity.favorite_movies.values()){
                        try {
                            movies_favorites.add(value);
                            JSONObject movie = new JSONObject(value);
                            posters[i] = movie.getString("poster_path");
                            i++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONRequstedResult = new ArrayList<>(Arrays.asList(posters));
                    if(MoviesView.movie_posters == null)
                        MoviesView.movie_posters = JSONRequstedResult;



                    ImageAdapter adapter = new ImageAdapter(progressBar.getContext(),JSONRequstedResult,moviesView.getColumnWidth());
                    moviesView.setAdapter(adapter);

                    if(posters.length == 0)
                        Toast.makeText(moviesView.getContext(),"You have no favorite movies",Toast.LENGTH_LONG).show();

                    return;

                }
                else {
                    getActivity().setTitle("New Movies");
                    url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + Utils.API_KEY + "&language=en-US";
                }

                new Utils.getJSONFormat("poster_path").execute(url);

            } else {
                Toast.makeText(getActivity(), "unable to connect to internet", Toast.LENGTH_LONG).show();
            }
        }
    }

}
