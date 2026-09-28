package com.cinetrack.model;

import java.util.List;

public class Movie {
    private int movieId;
    private String title;
    private Integer releaseYear;
    private Integer runtimeMin;
    private String synopsis;
    private String posterUrl;
    private Double avgRating;
    private List<String> genres;
    private String avgRatingFormatted;
    private int reviewCount;

    public Movie(int movieId, String title, Integer releaseYear, Integer runtimeMin, 
        String synopsis, String posterUrl, Double avgRating) {

        this.movieId = movieId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.runtimeMin = runtimeMin;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.avgRating = avgRating;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Integer getRuntimeMin() {
        return runtimeMin;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getAvgRatingFormatted() {
        return avgRatingFormatted;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setAvgRatingFormatted(String avgRatingFormatted) {
        this.avgRatingFormatted = avgRatingFormatted;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
