package com.cinetrack.service;

import com.cinetrack.model.WatchlistEntry;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class WatchlistService {

    private final DataSource dataSource;

    public WatchlistService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<WatchlistEntry> getUserWatchlist(int userId) {
        List<WatchlistEntry> list = new ArrayList<>();

        String sql = """
            SELECT w.status, m.movie_id, m.title, m.poster_url
            FROM WATCHLIST_ENTRY w
            JOIN MOVIE m ON w.movie_id = m.movie_id
            WHERE w.user_id = ?
            ORDER BY w.status
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new WatchlistEntry(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("poster_url"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Watchlist error: " + e.getMessage());
        }

        return list;
    }

    public List<WatchlistEntry> getByStatus(int userId, String status) {
        List<WatchlistEntry> list = new ArrayList<>();

        String sql = """
            SELECT w.status, m.movie_id, m.title, m.poster_url
            FROM WATCHLIST_ENTRY w
            JOIN MOVIE m ON w.movie_id = m.movie_id
            WHERE w.user_id = ? AND w.status = ?
            ORDER BY w.added_at DESC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, status);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new WatchlistEntry(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("poster_url"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Get watchlist by status error: " + e.getMessage());
        }

        return list;
    }

    public void updateWatchlistStatus(int userId, int movieId, String status) {
        String sql = """
            UPDATE WATCHLIST_ENTRY
            SET status = ?
            WHERE user_id = ? AND movie_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.setInt(3, movieId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update watchlist status error: " + e.getMessage());
        }
    }

    public void deleteWatchlistEntry(int userId, int movieId) {
        String sql = """
            DELETE FROM WATCHLIST_ENTRY
            WHERE user_id = ? AND movie_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Delete watchlist entry error: " + e.getMessage());
        }
    }

    public int getCountByStatus(int userId, String status) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM WATCHLIST_ENTRY
            WHERE user_id = ? AND status = ?
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, status);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Get count by status error: " + e.getMessage());
        }

        return 0;
    }

    public void addToWatchlist(int userId, int movieId, String status) {
        String sql = """
            INSERT INTO WATCHLIST_ENTRY (user_id, movie_id, status)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE status = VALUES(status)
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setString(3, status);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Add to watchlist error: " + e.getMessage());
        }
    }    

    public String getUserMovieStatus(int userId, int movieId) {
        String sql = """
            SELECT status
            FROM WATCHLIST_ENTRY
            WHERE user_id = ? AND movie_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }

        } catch (SQLException e) {
            System.out.println("Get status error: " + e.getMessage());
        }

        return null; // not in watchlist
    }
}