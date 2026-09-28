package com.cinetrack.model;

public class WatchlistEntry {
    private int movieId;
    private String title;
    private String posterUrl;
    private String status;

    public WatchlistEntry(int movieId, String title, String posterUrl, String status) {
        this.movieId = movieId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.status = status;
    }

    public int getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public String getStatus() { return status; }
}