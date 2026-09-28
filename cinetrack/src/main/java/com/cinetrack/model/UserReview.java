package com.cinetrack.model;

import java.time.LocalDateTime;

public class UserReview {
    private int reviewId;
    private int movieId;
    private String movieTitle;
    private String posterUrl;
    private int rating;
    private String body;
    private LocalDateTime createdAt;

    public UserReview() {
    }

    public UserReview(int reviewId, int movieId, String movieTitle, String posterUrl,
                      int rating, String body, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.body = body;
        this.createdAt = createdAt;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public int getRating() {
        return rating;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
