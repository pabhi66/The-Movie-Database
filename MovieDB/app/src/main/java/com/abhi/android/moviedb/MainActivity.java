package com.abhi.android.moviedb;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final Uri CONTENT_URL = Uri.parse(SaveFavorites.URL);
    static ContentResolver resolver;
    static HashMap<String, String> favorite_movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolver = getContentResolver();
        getMovies();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main, new MoviesView())
                    .commit();
        }
    }

    public static void getMovies(){
        String[] projection = new String[]{"title", "movieJson"};

        Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);

        favorite_movies = new HashMap<>();

        assert cursor != null;
        if(cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String movieJson = cursor.getString(cursor.getColumnIndex("movieJson"));
                favorite_movies.put(title,movieJson);
            }while (cursor.moveToNext());
        }
    }


    /**
     * create option menu
     * @param menu menu
     * @return true = create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * do what happens when you click item in meny
     * @param item menu item
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get clicked menu item id
        int id = item.getItemId();

        //toast

        //switch to handle different menu items
        switch (id){
            //if sort by popularity is clicked
            case R.id.sort_by_popularity : {
                //replace toast with method
                Utils.sort_by_ratings = false;
                Utils.sort_by_favorites = false;
                Utils.sort_by_popularity = true;
                MoviesView.movie_posters = null;


                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, new MoviesView())
                        .commit();
                return true;
            }
            //if sort by ratings is clicked
            case R.id.sort_by_ratings : {
                //replace toast with method
                Utils.sort_by_popularity=false;
                Utils.sort_by_favorites = false;
                Utils.sort_by_ratings = true;
                MoviesView.movie_posters = null;


                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, new MoviesView())
                        .commit();
                return true;
            }

            case R.id.favorites:{
                Utils.sort_by_popularity=false;
                Utils.sort_by_ratings = false;
                Utils.sort_by_favorites = true;
                MoviesView.movie_posters = null;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, new MoviesView())
                        .commit();
                return true;
            }
            //default action
            default:{
                Utils.sort_by_popularity=false;
                Utils.sort_by_ratings = false;
                Utils.sort_by_favorites = false;
                MoviesView.movie_posters = null;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, new MoviesView())
                        .commit();

            }

        }

        return super.onOptionsItemSelected(item);
    }
}
