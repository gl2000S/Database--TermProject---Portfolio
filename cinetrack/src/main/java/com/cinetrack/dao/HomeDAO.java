package com.cinetrack.dao;

import com.cinetrack.model.Actor;
import com.cinetrack.model.Director;
import com.cinetrack.model.Movie;
import com.cinetrack.model.RecentActivityItem;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HomeDAO {

    private final DataSource dataSource;

    public HomeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Movie> getMoviesPage(String query, int offset, int limit) {
        List<Movie> movies = new ArrayList<>();
        boolean searching = query != null && !query.isBlank();
        String sql = searching
                ? """
                  SELECT movie_id, title, release_year, runtime_min, synopsis, poster_url, avg_rating
                  FROM MOVIE
                  WHERE title LIKE ?
                  ORDER BY release_year DESC, title
                  LIMIT ? OFFSET ?
                  """
                : """
                  SELECT movie_id, title, release_year, runtime_min, synopsis, poster_url, avg_rating
                  FROM MOVIE
                  ORDER BY release_year DESC, title
                  LIMIT ? OFFSET ?
                  """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (searching) {
                stmt.setString(1, "%" + query.trim() + "%");
                stmt.setInt(2, limit);
                stmt.setInt(3, offset);
            } else {
                stmt.setInt(1, limit);
                stmt.setInt(2, offset);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(mapMovie(rs));
            }
        } catch (Exception e) {
            System.err.println("HomeDAO.getMoviesPage error: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }

    public List<Director> getDirectorsPage(int offset, int limit) {
        List<Director> directors = new ArrayList<>();
        String sql = """
                SELECT director_id, name, bio, birthdate, photo_url
                FROM DIRECTOR
                ORDER BY name
                LIMIT ? OFFSET ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date bd = rs.getDate("birthdate");
                directors.add(new Director(
                        rs.getInt("director_id"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        bd != null ? bd.toLocalDate() : null,
                        rs.getString("photo_url")
                ));
            }
        } catch (Exception e) {
            System.err.println("HomeDAO.getDirectorsPage error: " + e.getMessage());
            e.printStackTrace();
        }
        return directors;
    }

    public List<Actor> getActorsPage(int offset, int limit) {
        List<Actor> actors = new ArrayList<>();
        String sql = """
                SELECT actor_id, name, bio, birthdate, photo_url
                FROM ACTOR
                ORDER BY name
                LIMIT ? OFFSET ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date bd = rs.getDate("birthdate");
                actors.add(new Actor(
                        rs.getInt("actor_id"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        bd != null ? bd.toLocalDate() : null,
                        rs.getString("photo_url")
                ));
            }
        } catch (Exception e) {
            System.err.println("HomeDAO.getActorsPage error: " + e.getMessage());
            e.printStackTrace();
        }
        return actors;
    }

    public List<RecentActivityItem> getRecentActivity(int limit) {
        List<RecentActivityItem> items = new ArrayList<>();
        String sql = """
                SELECT u.username, m.movie_id, m.title AS movie_title, r.rating, r.body
                FROM REVIEW r
                JOIN USER u ON r.user_id = u.user_id
                JOIN MOVIE m ON r.movie_id = m.movie_id
                ORDER BY r.created_at DESC
                LIMIT ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new RecentActivityItem(
                        rs.getString("username"),
                        rs.getInt("movie_id"),
                        rs.getString("movie_title"),
                        rs.getInt("rating"),
                        rs.getString("body")
                ));
            }
        } catch (Exception e) {
            System.err.println("HomeDAO.getRecentActivity error: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    private Movie mapMovie(ResultSet rs) throws SQLException {
        double avgDouble = rs.getDouble("avg_rating");
        Double avgRating = rs.wasNull() ? null : avgDouble;

        int yearInt = rs.getInt("release_year");
        Integer releaseYear = rs.wasNull() ? null : yearInt;

        int runtimeInt = rs.getInt("runtime_min");
        Integer runtimeMin = rs.wasNull() ? null : runtimeInt;

        return new Movie(
                rs.getInt("movie_id"),
                rs.getString("title"),
                releaseYear,
                runtimeMin,
                rs.getString("synopsis"),
                rs.getString("poster_url"),
                avgRating
        );
    }
}
