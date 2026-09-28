package com.cinetrack.model;

public class UserProfileStats {
    private int reviewCount;
    private int followerCount;
    private int followingCount;

    public UserProfileStats() {
    }

    public UserProfileStats(int reviewCount, int followerCount, int followingCount) {
        this.reviewCount = reviewCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }
}
