package com.cinetrack.service;

import com.cinetrack.model.User;
import com.cinetrack.model.UserProfileStats;
import com.cinetrack.model.UserReview;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User getUserById(int userId) {
        String sql = """
                SELECT user_id, username, email, password_hash, bio, created_at
                FROM `USER`
                WHERE user_id = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("bio"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.out.println("Get user error: " + e.getMessage());
        }

        return null;
    }

    public UserProfileStats getUserStats(int userId) {
        String sql = """
                SELECT
                    COUNT(DISTINCT r.review_id) AS review_count,
                    COUNT(DISTINCT f1.followee_id) AS following_count,
                    COUNT(DISTINCT f2.follower_id) AS follower_count
                FROM `USER` u
                LEFT JOIN REVIEW r ON u.user_id = r.user_id
                LEFT JOIN FOLLOWS f1 ON u.user_id = f1.follower_id
                LEFT JOIN FOLLOWS f2 ON u.user_id = f2.followee_id
                WHERE u.user_id = ?
                GROUP BY u.user_id
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserProfileStats(
                        rs.getInt("review_count"),
                        rs.getInt("follower_count"),
                        rs.getInt("following_count")
                );
            }

        } catch (SQLException e) {
            System.out.println("Get user stats error: " + e.getMessage());
        }

        return new UserProfileStats(0, 0, 0);
    }

    public List<UserReview> getRecentReviewsByUser(int userId) {
        List<UserReview> reviews = new ArrayList<>();

        String sql = """
                SELECT
                    r.review_id,
                    r.rating,
                    r.body,
                    r.created_at,
                    m.movie_id,
                    m.title,
                    m.poster_url
                FROM REVIEW r
                JOIN MOVIE m ON r.movie_id = m.movie_id
                WHERE r.user_id = ?
                ORDER BY r.created_at DESC
                LIMIT 10
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reviews.add(new UserReview(
                        rs.getInt("review_id"),
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("poster_url"),
                        rs.getInt("rating"),
                        rs.getString("body"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            System.out.println("Get recent reviews error: " + e.getMessage());
        }

        return reviews;
    }
}
