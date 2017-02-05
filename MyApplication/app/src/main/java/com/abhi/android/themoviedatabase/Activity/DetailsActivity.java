package com.abhi.android.themoviedatabase.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.abhi.android.themoviedatabase.Fragments.MovieDetail;
import com.abhi.android.themoviedatabase.R;

/**
 * Created by abhi on 2/4/17.
 */

public class DetailsActivity extends AppCompatActivity {

    public static String movieJSONString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);


        movieJSONString = getIntent().getStringExtra("movie");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details, new MovieDetail())
                    .commit();
        }
    }
}
