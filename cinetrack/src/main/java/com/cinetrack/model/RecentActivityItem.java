package com.cinetrack.model;

public class RecentActivityItem {
    private String username;
    private int movieId;
    private String movieTitle;
    private int rating;
    private String body;

    public RecentActivityItem(String username, int movieId, String movieTitle, int rating, String body) {
        this.username = username;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rating = rating;
        this.body = body;
    }

    public String getUsername()   { return username; }
    public int getMovieId()       { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public int getRating()        { return rating; }
    public String getBody()       { return body; }
}
