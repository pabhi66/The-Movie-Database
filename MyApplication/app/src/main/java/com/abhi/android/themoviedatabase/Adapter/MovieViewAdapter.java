package com.abhi.android.themoviedatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.android.themoviedatabase.Activity.MovieDetail;
import com.abhi.android.themoviedatabase.Model.Movie;
import com.abhi.android.themoviedatabase.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;



public class MovieViewAdapter extends RecyclerView.Adapter<MovieViewAdapter.MyViewHolder> {

    private ArrayList<Movie> mMovieList;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public MovieViewAdapter(Context mContext, ArrayList<Movie> mMovieList) {
        this.mContext = mContext;
        this.mMovieList = mMovieList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_popular_movies, parent, false);



        return new MyViewHolder(itemView);
    }


    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Movie movie = mMovieList.get(position);
        holder.title.setText(movie.getMovie_name());

        Picasso.with(mContext).load(movie.getMovie_poster_path()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movie = mMovieList.get(position).getMovie().toString();
                Intent intent = new Intent(mContext, MovieDetail.class);
                intent.putExtra("movie",movie);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }


}
