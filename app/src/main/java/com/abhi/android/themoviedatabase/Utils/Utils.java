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
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Util class that do certain things
 * Check if internet is available
 * connect to the internet
 * get device size
 * get specific paths from the Json string
 */
public class Utils{

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

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}
