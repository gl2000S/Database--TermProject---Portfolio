package com.cinetrack.model;

public class DirectorFilmEntry {
    private int movieId;
    private String title;
    private Integer releaseYear;
    private String posterUrl;
    private Double avgRating;

    public DirectorFilmEntry() {}

    public DirectorFilmEntry(int movieId, String title, Integer releaseYear,
                             String posterUrl, Double avgRating) {
        this.movieId = movieId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.posterUrl = posterUrl;
        this.avgRating = avgRating;
    }

    public int getMovieId()         { return movieId; }
    public String getTitle()        { return title; }
    public Integer getReleaseYear() { return releaseYear; }
    public String getPosterUrl()    { return posterUrl; }
    public Double getAvgRating()    { return avgRating; }
}
