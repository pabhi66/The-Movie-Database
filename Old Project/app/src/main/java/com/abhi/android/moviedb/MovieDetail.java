package com.abhi.android.moviedb;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class MovieDetail extends AppCompatActivity {

    TextView movieName;
    ImageView imageView;
    RatingBar rating;
    TextView release_date;
    TextView plot;
    Button trailer_button;
    Button trailer_button2;
    TextView reviews;
//    Button favorite_button;

    JSONObject movie;


    private Menu menu;
    int menuClickeddCount = 0;

    static String movie_name;
    static String movie_poster_path;
    static String movie_trailer1;
    static String movie_trailer2;
    static String movie_plot;
    static String movie_rating;
    static String movie_reviews;
    static String movie_release_date;
    static int movie_id;

    static String movieJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //get and set views from movie detail layout
        movieName = (TextView) findViewById(R.id.movie_name);
        imageView = (ImageView) findViewById(R.id.imageView);
        rating = (RatingBar) findViewById(R.id.rating);
        release_date = (TextView) findViewById(R.id.release_date);
        plot = (TextView) findViewById(R.id.plot);
        trailer_button = (Button) findViewById(R.id.trailer);
        trailer_button2 = (Button) findViewById(R.id.trailer1);
        reviews = (TextView) findViewById(R.id.reviews);
//        favorite_button = (Button) findViewById(R.id.favorite);


        try {
            //get extra intent
            movieJsonString = getIntent().getStringExtra("movie");
            movie = new JSONObject(movieJsonString);
            movie_name = movie.getString("original_title");
            movie_poster_path = movie.getString("poster_path");
            movie_id = movie.getInt("id");
            movie_rating = movie.getString("vote_average");
            movie_release_date = movie.getString("release_date");
            movie_plot = movie.getString("overview");

//            if(MainActivity.favorite_movies.containsKey(movie_name)){
//                favorite_button.setText("Remove from Favorite");
//            }


                //set movie name
            movieName.setText(movie_name);

            setTitle(movie_name);

            //set poster path
            String poster = "http://image.tmdb.org/t/p/w185" + movie_poster_path;
            Picasso.with(this).load(poster).resize(500,750).into(imageView);

            //set plot
            plot.setText(movie_plot);

            //set rating stars
            String rati = movie_rating;
            float rate = Float.parseFloat(rati);
            if(rate == 0){
                rate = 0;
                Toast.makeText(this,"This move has no rating",Toast.LENGTH_LONG).show();
            }
            rating.setRating(rate);

            //set release date
            release_date.setText("Release Date: " + movie_release_date);

            //set trailer button
            onTrailerButtonClicked();
            trailer_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //onTrailerButtonClicked(1);
                    Uri webpage = Uri.parse(movie_trailer1);
                Intent intent= new Intent(Intent.ACTION_VIEW, webpage);

                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
                }
            });

            trailer_button2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //onTrailerButtonClicked(2);
                    Uri webpage = Uri.parse(movie_trailer2);
                    Intent intent= new Intent(Intent.ACTION_VIEW, webpage);

                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
            });

            //get backdrop poster
            //String backdrop_path = "http://image.tmdb.org/t/p/original" + movie.getString("backdrop_path");

            String review_url = "https://api.themoviedb.org/3/movie/" + movie_id + "/reviews?api_key=" + Utils.API_KEY;
            new loadReviews().execute(review_url);


//            favorite_button.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//
//                    Toast toast = Toast.makeText(favorite_button.getContext(),"",Toast.LENGTH_SHORT);
//
//                    if(favorite_button.getText().equals("Add to Favorite")){
//                        favorite_button.setText("Remove from Favorite");
//
//                        ContentValues values = new ContentValues();
//                        values.put(SaveFavorites.title,movie_name);
//                        values.put(SaveFavorites.movieJson, movieJsonString);
//
//                        getContentResolver().insert(SaveFavorites.CONTENT_URL,values);
//                        MainActivity.getMovies();
//
//                        toast.setText("Movie added to favorite");
//                        toast.show();
//
//
//                    }
//                    else if(favorite_button.getText().equals("Remove from Favorite")){
//                        favorite_button.setText("Add to Favorite");
//                        getContentResolver().delete(Uri.parse(SaveFavorites.URL), "title=?", new String[]{movie_name});
//                        MainActivity.getMovies();
//                    }
//                }
//            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        this.menu = menu;
        if(MainActivity.favorite_movies.containsKey(movie_name)){
            menuClickeddCount = 1;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Toast toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        switch (id){
            case R.id.action_favorite: {
                if(menuClickeddCount == 0) {
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite));
                    menuClickeddCount=1;
                    toast.setText("Movie added to favorite");
                    toast.show();

                    ContentValues values = new ContentValues();
                    values.put(SaveFavorites.title,movie_name);
                    values.put(SaveFavorites.movieJson, movieJsonString);

                    getContentResolver().insert(SaveFavorites.CONTENT_URL,values);
                    MainActivity.getMovies();

                }else if(menuClickeddCount == 1){
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border));
                    menuClickeddCount=0;
                    toast.setText("Movie removed from favorite");
                    toast.show();

                    MoviesView.remove = true;


                    getContentResolver().delete(Uri.parse(SaveFavorites.URL), "title=?", new String[]{movie_name});
                    MainActivity.getMovies();

                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTrailerButtonClicked() {
        String trailerUrl = "https://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=" + Utils.API_KEY;
        new loadTrailer().execute(trailerUrl);
    }



    public class loadTrailer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return Utils.connectToInternet(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String[] result = Utils.getPathsFromJSON(s,"key");
                String trailer = "";
                if(result.length >= 1){
                    trailer = "https://www.youtube.com/watch?v=" + result[0];
                    movie_trailer1 = trailer;
                }
                if(result.length >=2){
                    trailer = "https://www.youtube.com/watch?v=" + result[1];
                    movie_trailer2=trailer;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class loadReviews extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                return Utils.connectToInternet(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                String[] authors = Utils.getPathsFromJSON(s,"author");
                String[] review = Utils.getPathsFromJSON(s,"content");

                if(authors.length == 0 || review.length == 0){
                    reviews.setText("There are no reviews for this movie");
                }else{
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < review.length; i++){
                        sb.append(authors[i]).append(": ").append(review[i]).append("\n===============================\n");
                    }

                    movie_reviews = sb.toString();

                    reviews.setText(sb.toString());
                }


            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
}
