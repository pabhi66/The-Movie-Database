package com.abhi.android.moviedb;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by abhi on 1/10/17.
 */
public class ImageAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> movie_posterss;
    int columnWidth;

    /**
     * image adapter
     * @param context context
     * @param movie_posters array of movie poster links
     * @param columnWidth width of gridview
     */
    public ImageAdapter(Context context, ArrayList<String> movie_posters, int columnWidth) {
        this.context = context;
        this.movie_posterss = movie_posters;
        this.columnWidth = columnWidth;
    }

    /**
     * returns the number of posters
     * @return
     */
    @Override
    public int getCount() {
        if(movie_posterss == null)
            return 0;
        else return movie_posterss.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * sets the image to gridview
     * @param position position in grid view
     * @param convertView convert view
     * @param parent parent
     * @return return imageview
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if(convertView== null){
            imageView = new ImageView(context);
        }else{
            imageView = (ImageView) convertView;
        }

        String Imagepath = "http://image.tmdb.org/t/p/w185" + movie_posterss.get(position);

        if(columnWidth == 0)
            columnWidth = 360;

        Picasso.with(context).load(Imagepath).resize(columnWidth, (int)  ((columnWidth) * 1.4)).into(imageView);


        return imageView;
    }
}
