package com.gg.rcmals.domain;

import scala.Serializable;

/**
 * @author daigouwei
 * @date 2018/11/22
 */
public class MovieRating implements Serializable {
    private static final long serialVersionUID = -3446638590469848609L;

    private int userId;
    private int movieId;
    private float rating;
    private long timestamp;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
