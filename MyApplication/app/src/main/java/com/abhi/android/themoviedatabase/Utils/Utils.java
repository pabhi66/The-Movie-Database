package com.abhi.android.themoviedatabase.Utils;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Util class that do certain things
 * Check if internet is available
 * connect to the internet
 * get device size
 * get specific paths from the Json string
 */
public class Utils implements LoaderManager.LoaderCallbacks<String>{

    //the movie data base API KEY and sorting
    public static final String API_KEY = "ec20d71f2808beb5d284838837fab55d";
    public static boolean sort_by_popularity = false;
    public static boolean sort_by_ratings = false;
    public static boolean sort_by_favorites = false;
    public static boolean sort_by_new_movies = false;

    public static final int POSTER_LOADER = 1;
    public static final int TRAILER_LOADER = 2;
    public static final int REVIEWS_LOADER = 3;

    public static double screenSize;
    public static int width;

    static ArrayList<String> JSONRequstedResult;

    //JSON Results
    //static JSONArray jsonArrayObject;


    /**
     * check if the internet is available on the device
     * @param context context
     * @return true if intnet is available
     */
    public static boolean isInternetAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * get the device size
     * @param context context
     */
    public static void getDeviceSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        //int widthInPixel = display.getWidth();
        //int heightInPixel = display.getHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        double x = Math.pow(displayMetrics.widthPixels/displayMetrics.xdpi,2);
        double y = Math.pow(displayMetrics.heightPixels/displayMetrics.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);

        Point size = new Point();
        display.getSize(size);
        //int height = size.y;
        width = size.x;

        screenSize = screenInches;
    }

    /**
     * connect to the internet
     */
    public static String connectToInternet(String url) throws IOException{
        URL link = new URL(url);
        HttpURLConnection httpURLConnection= (HttpURLConnection) link.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();

        //read the connection
        InputStream inputStream = httpURLConnection.getInputStream();
        StringBuilder stringBuffer = new StringBuilder();
        if (inputStream == null)
            return null;

        //read the entire JSON text
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line).append("\n");
        }
        if (stringBuffer.length() == 0)
            return null;

        //return JSONResult in string
        return stringBuffer.toString();
    }

    /**
     * get movie posters from JSON string
     * @param jsonResult Json result
     * @return movie posters paths
     * @throws JSONException
     */
    public static String[] getPathsFromJSON(String jsonResult, String key) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonResult);

        JSONArray arrayResult= jsonObject.getJSONArray("results");
        String[] result = new String[arrayResult.length()];
        //jsonArrayObject = arrayResult;
//        if(key.equals("poster_path"))
//            MoviesView.results = arrayResult;

        for(int i = 0; i < arrayResult.length(); i++){
            JSONObject movie = arrayResult.getJSONObject(i);
            String moviePath = movie.getString(key);
            result[i] = moviePath;
        }
        return result;
    }

    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return null;
//        return new AsyncTaskLoader<String>() {
//
//
//            @Override
//            protected void onStartLoading() {
//                //progressBar.setVisibility(View.VISIBLE);
//                super.onStartLoading();
//                if(args == null){
//                    return;
//                }
//
//                if (takeContentChanged()) {
//                    forceLoad();
//                }
//
//                forceLoad();
//            }
//
//
//            @Override
//            public String loadInBackground() {
//                String url = null;
//
//                if(id == POSTER_LOADER) {
//
//                    url =args.getString("posters");
//                }else if(id == TRAILER_LOADER){
//                    url = args.getString("trailers");
//                }else if(id == REVIEWS_LOADER){
//                    url = args.getString("reviews");
//                }
//
//                if ( url == null || url.isEmpty())
//                    return null;
//                try {
//                    return Utils.connectToInternet(url);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        //progressBar.setVisibility(View.INVISIBLE);

        if(loader.getId() == POSTER_LOADER) {

            if (data != null) {
                try {
                    JSONRequstedResult = new ArrayList<>(Arrays.asList(Utils.getPathsFromJSON(data, "poster_path")));

//                    if (MoviesView.movie_posters == null)
//                        MoviesView.movie_posters = JSONRequstedResult;
//
//
//                    ImageAdapter adapter = new ImageAdapter(progressBar.getContext(), JSONRequstedResult, moviesView.getColumnWidth());
//                    moviesView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        else if(loader.getId() == TRAILER_LOADER){
            try {
                String[] result = Utils.getPathsFromJSON(data, "key");
                String trailer;
                if (result.length >= 1) {
                    trailer = TinyUrls.youtube + result[0];
                    //movie_trailer1 = trailer;
                }
                if (result.length >= 2) {
                    trailer = TinyUrls.youtube + result[1];
                    //movie_trailer2 = trailer;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(loader.getId() == REVIEWS_LOADER){
            try{
                String[] authors = Utils.getPathsFromJSON(data,"author");
                String[] review = Utils.getPathsFromJSON(data,"content");

                if(authors.length == 0 || review.length == 0){
                    //reviews.setText(R.string.no_reviews);
                   // movie_reviews = getString(R.string.no_reviews);
                }else{
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < review.length; i++){
                        sb.append(authors[i]).append(": ").append(review[i]).append("\n===============================\n");
                    }

                    //movie_reviews = sb.toString();

                    //reviews.setText(sb.toString());
                }


            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

//    private static void executeLoader(String item, String url, int id){
//        Bundle queryBundle= new Bundle();
//        queryBundle.putString(item,url);
//
//
//
//        LoaderManager loaderManager = getLoaderManager();
//        Loader<String> trailerLoader = loaderManager.getLoader(id);
//
//        if(trailerLoader== null){
//            loaderManager.initLoader(id,queryBundle,this);
//        }else{
//            loaderManager.restartLoader(id,queryBundle,this);
//        }
//    }
}
