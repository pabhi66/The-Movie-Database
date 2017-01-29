package com.abhi.android.themoviedatabase.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.abhi.android.themoviedatabase.R;
import com.abhi.android.themoviedatabase.Utils.TinyUrls;
import com.abhi.android.themoviedatabase.Utils.Utils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    public static String movieJSONString;
    private JSONObject movie;

    private static final int TRAILER_LOADER = 4;
    private static final int REVIEW_LOADER = 5;
    private static final int MOVIE_LOADER = 6;

    private String trailer1, trailer2;

    private String movie_name, movie_poster_path, movie_backdrop_path, movie_plot, movie_rating, movie_release_date;
    private String movie_details;

    @BindView(R.id.title)
    TextView movieName;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.rate)
    TextView rating;
    @BindView(R.id.date_status)
    TextView release_date;
    @BindView(R.id.overview)
    TextView plot;
    @BindView(R.id.tagline) TextView tagline;
    @BindView(R.id.duration) TextView runtime;
    @BindView(R.id.trailer) Button trailer_button;
    @BindView(R.id.trailer1) Button trailer_button2;
    @BindView(R.id.reviews)
    TextView reviews;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this, this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        try {
            movieJSONString = getIntent().getStringExtra("movie");
            movie = new JSONObject(movieJSONString);

            movie_name = movie.getString("original_title");
            int movie_id = movie.getInt("id");

            String url = TinyUrls.movie_url + movie_id + TinyUrls.review_addon + Utils.API_KEY;
            executeLoader("reviews",url,REVIEW_LOADER);
            url = TinyUrls.movie_url + movie_id + "/videos?api_key=" + Utils.API_KEY;
            executeLoader("trailers",url,TRAILER_LOADER);

            url = "https://api.themoviedb.org/3/movie/" + movie_id + "?api_key=" + Utils.API_KEY;
            executeLoader("movie",url,MOVIE_LOADER);


            movie_poster_path = movie.getString("poster_path");
            movie_backdrop_path = movie.getString("backdrop_path");
            movie_plot = movie.getString("overview");
            movie_rating = movie.getString("vote_average");
            movie_release_date = movie.getString("release_date");

            String posterURL = "https://image.tmdb.org/t/p/w185" + movie_poster_path;
            String backdropURL = "https://image.tmdb.org/t/p/w1280" + movie_backdrop_path;

            Picasso.with(this).load(backdropURL).into((ImageView) findViewById(R.id.backdrop));
            Picasso.with(this).load(posterURL).into(imageView);

            movieName.setText(movie_name);
            plot.setText(movie_plot);
            release_date.setText(movie_release_date);

            trailer_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    launchTrailer(trailer1);
                }
            });

            trailer_button2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    launchTrailer(trailer2);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * launch trailer when trailer button is clicked
     * @param url trailer link
     */
    private void launchTrailer(String url){
        Uri webpage = Uri.parse(url);
        Intent intent= new Intent(Intent.ACTION_VIEW, webpage);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(movie_name);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(movie_name);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String url = "";
                if (id == TRAILER_LOADER) {
                    url = args.getString("trailers");
                } else if (id == REVIEW_LOADER) {
                    url = args.getString("reviews");
                }else if(id == MOVIE_LOADER ){
                    url = args.getString("movie");
                }

                if (url == null || url.isEmpty())
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
        if (loader.getId() == TRAILER_LOADER) {

            try {
                String[] result = Utils.getPathsFromJSON(data, "key");
                String trailer;
                if (result.length >= 1) {
                    trailer = TinyUrls.youtube + result[0];
                    trailer1 = trailer;
                }
                if (result.length >= 2) {
                    trailer = TinyUrls.youtube + result[1];
                    trailer2 = trailer;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (loader.getId() == REVIEW_LOADER) {
            try {
                String[] authors = Utils.getPathsFromJSON(data, "author");
                String[] review = Utils.getPathsFromJSON(data, "content");

                if (authors.length == 0 || review.length == 0) {
                    reviews.setText("There are no reviews for this movie");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < review.length; i++) {
                        sb.append(authors[i]).append(": ").append(review[i]).append("\n===============================\n");
                    }

                    reviews.setText(sb.toString());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(loader.getId() == MOVIE_LOADER){
            movie_details = data;
            try {
                movie = new JSONObject(data);
                tagline.setText("\"" + movie.getString("tagline") + "\"");
                runtime.setText(movie.getString("runtime") + " minutes");
                rating.setText(movie.getString("vote_average") + "/10");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void executeLoader(String item, String url, int id) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(item, url);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailerLoader = loaderManager.getLoader(id);

        if (trailerLoader == null) {
            loaderManager.initLoader(id, queryBundle, this);
        } else {
            loaderManager.restartLoader(id, queryBundle, this);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MovieDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
