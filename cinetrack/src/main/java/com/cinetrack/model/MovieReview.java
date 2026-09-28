package com.cinetrack.model;

import java.time.LocalDateTime;

public class MovieReview {
    private int reviewId;
    private String username;
    private int userId;
    private int rating;
    private String body;
    private LocalDateTime createdAt;


    public MovieReview(int reviewId, String username, int userId, int rating, 
        String body, LocalDateTime createdAt) {

        this.reviewId = reviewId;
        this.username = username;
        this.userId = userId;
        this.rating = rating;
        this.body = body;
        this.createdAt = createdAt;
    }


    public int getReviewId() {
        return reviewId;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
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
