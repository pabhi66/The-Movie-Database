package com.abhi.android.themoviedatabase.Activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.abhi.android.themoviedatabase.Fragments.FavoriteMovies;
import com.abhi.android.themoviedatabase.Fragments.MainActivityFragment;
import com.abhi.android.themoviedatabase.Fragments.NewMovies;
import com.abhi.android.themoviedatabase.Fragments.PopularMovies;
import com.abhi.android.themoviedatabase.Fragments.SearchFragment;
import com.abhi.android.themoviedatabase.Fragments.TopRatedMovies;
import com.abhi.android.themoviedatabase.R;
import com.abhi.android.themoviedatabase.Utils.SaveFavorites;
import com.abhi.android.themoviedatabase.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * main activity of the app
 */

public class MainActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    static final Uri CONTENT_URL = Uri.parse(SaveFavorites.URL);
    static ContentResolver resolver;
    public static HashMap<String, String> favorite_movies;

    public static String searchQuery;


    //load icons on top of the tool bar
    private int[] tabIcons = {
            R.drawable.popular_movies,
            R.drawable.top_rated,
            R.drawable.new_movies,
            R.drawable.heart_filled
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get device size
        Utils.getDeviceSize(this);

        //get content resolver and load user's favorite stored movies
        resolver = getContentResolver();
        getMovies();

        //set up tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //set up view pager
        ViewPager mPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mPager);


        //assign slider to tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
        //setupTabIcons();

    }

    /**
     * get saved movies from content manager
     */
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


        cursor.close();
    }

    /**
     * set tab icons on the tool bar
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    /**
     * set up view pager
     * @param viewPager view pager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PopularMovies(), "Popular");
        adapter.addFragment(new TopRatedMovies(), "Top Rated");
        adapter.addFragment(new NewMovies(), "New");
        adapter.addFragment(new FavoriteMovies(), "Favorites");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //getFragmentManager().beginTransaction().replace(R.id.main, new SearchFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ViewPagerAdapter  extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
