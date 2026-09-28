package com.cinetrack.dao;

import com.cinetrack.model.Director;
import com.cinetrack.model.DirectorFilmEntry;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DirectorDAO {

    private final DataSource dataSource;

    public DirectorDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Query 18a — director profile
    public Director getDirectorById(int directorId) {
        String sql = """
                SELECT director_id, name, bio, birthdate, photo_url
                FROM DIRECTOR
                WHERE director_id = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, directorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date bd = rs.getDate("birthdate");
                return new Director(
                        rs.getInt("director_id"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        bd != null ? bd.toLocalDate() : null,
                        rs.getString("photo_url")
                );
            }

        } catch (SQLException e) {
            System.out.println("DirectorDAO.getDirectorById error: " + e.getMessage());
        }

        return null;
    }

    // Query 18b — filmography with avg review rating per film, ordered newest first
    public List<DirectorFilmEntry> getFilmography(int directorId) {
        List<DirectorFilmEntry> films = new ArrayList<>();

        String sql = """
                SELECT m.movie_id, m.title, m.release_year, m.poster_url,
                       AVG(r.rating) AS avg_rating
                FROM MOVIE_DIRECTOR md
                JOIN MOVIE m ON md.movie_id = m.movie_id
                LEFT JOIN REVIEW r ON m.movie_id = r.movie_id
                WHERE md.director_id = ?
                GROUP BY m.movie_id, m.title, m.release_year, m.poster_url
                ORDER BY m.release_year DESC
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, directorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int year = rs.getInt("release_year");
                double avg = rs.getDouble("avg_rating");
                films.add(new DirectorFilmEntry(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.wasNull() ? null : year,
                        rs.getString("poster_url"),
                        rs.wasNull() ? null : avg
                ));
            }

        } catch (SQLException e) {
            System.out.println("DirectorDAO.getFilmography error: " + e.getMessage());
        }

        return films;
    }
}
