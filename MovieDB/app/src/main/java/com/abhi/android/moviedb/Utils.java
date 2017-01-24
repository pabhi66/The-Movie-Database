package com.abhi.android.moviedb;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
 * Created by abhi on 1/10/17.
 */

public class Utils {

    //the movie data base API KEY and sorting
    static final String API_KEY = "ec20d71f2808beb5d284838837fab55d";
    static boolean sort_by_popularity = false;
    static boolean sort_by_ratings = false;
    static boolean sort_by_favorites = false;

    //screen size
    static int heightInPixel;
    static int widthInPixel;
    static double screenSize;
    static int height;
    static int width;

    //JSON Results
    //static String contentInJSONFormat;
    static ArrayList<String> JSONRequstedResult;
    static JSONArray jsonArrayObject;


    /**
     * check if the internet is available on the device
     * @param context
     * @return
     */
    public static boolean isInternetAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * get the device size
     * @param context
     */
    public static void getDeviceSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        widthInPixel = display.getWidth();
        heightInPixel = display.getHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        double x = Math.pow(displayMetrics.widthPixels/displayMetrics.xdpi,2);
        double y = Math.pow(displayMetrics.heightPixels/displayMetrics.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);

        Point size = new Point();
        display.getSize(size);
        height = size.y;
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
     * returns the string of JSON data
     */
    static class getJSONFormat extends AsyncTask<String, Void, String> {

        String requst_item;
        getJSONFormat(String item) {
            requst_item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MoviesView.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return connectToInternet(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            MoviesView.progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(s);
            //contentInJSONFormat = s;

            if(s != null) {
                try {
                    JSONRequstedResult = new ArrayList<>(Arrays.asList(getPathsFromJSON(s, requst_item)));

                    if(MoviesView.movie_posters == null)
                        MoviesView.movie_posters = JSONRequstedResult;



                    ImageAdapter adapter = new ImageAdapter(MoviesView.progressBar.getContext(),JSONRequstedResult,MoviesView.moviesView.getColumnWidth());
                    MoviesView.moviesView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
        jsonArrayObject = arrayResult;
        if(key.equals("poster_path"))
            MoviesView.results = arrayResult;

        for(int i = 0; i < arrayResult.length(); i++){
            JSONObject movie = arrayResult.getJSONObject(i);
            String moviePath = movie.getString(key);
            result[i] = moviePath;
        }
        return result;
    }
}
