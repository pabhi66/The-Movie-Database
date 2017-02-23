package com.abhi.android.themoviedatabase.Model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Movie class with all of its information
 */

public class Movie implements Serializable{
    private String movie_name;
    private String movie_poster_path;
    private String movie_trailer1;
    private String movie_trailer2;
    private String movie_plot;
    private String movie_rating;
    private String movie_reviews;
    private String movie_release_date;
    private int movie_id;
    private JSONObject movie;

    public Movie(String movie_name, String movie_poster_path, String movie_plot
        , String movie_rating, String movie_release_date, int movie_id, JSONObject movie){
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.movie_plot = movie_plot;
        this.movie_poster_path = movie_poster_path;
        this.movie_rating = movie_rating;
        this.movie_release_date = movie_release_date;
        this.movie = movie;
    }

    public JSONObject getMovie() {
        return movie;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public String getMovie_poster_path() {
        return movie_poster_path;
    }

    public String getMovie_trailer1() {
        return movie_trailer1;
    }

    public String getMovie_trailer2() {
        return movie_trailer2;
    }

    public String getMovie_plot() {
        return movie_plot;
    }

    public String getMovie_rating() {
        return movie_rating;
    }

    public String getMovie_reviews() {
        return movie_reviews;
    }

    public String getMovie_release_date() {
        return movie_release_date;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_trailer1(String movie_trailer1) {
        this.movie_trailer1 = movie_trailer1;
    }

    public void setMovie_trailer2(String movie_trailer2) {
        this.movie_trailer2 = movie_trailer2;
    }

    public void setMovie_reviews(String movie_reviews) {
        this.movie_reviews = movie_reviews;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movie_name='" + movie_name + '\'' +
                ", movie_poster_path='" + movie_poster_path + '\'' +
                ", movie_trailer1='" + movie_trailer1 + '\'' +
                ", movie_trailer2='" + movie_trailer2 + '\'' +
                ", movie_plot='" + movie_plot + '\'' +
                ", movie_rating='" + movie_rating + '\'' +
                ", movie_reviews='" + movie_reviews + '\'' +
                ", movie_release_date='" + movie_release_date + '\'' +
                ", movie_id=" + movie_id +
                '}';
    }

}
