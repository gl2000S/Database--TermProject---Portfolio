package com.cinetrack.dao;

import com.cinetrack.model.Actor;
import com.cinetrack.model.ActorFilmEntry;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActorDAO {

    private final DataSource dataSource;

    public ActorDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Query 17a — actor profile
    public Actor getActorById(int actorId) {
        String sql = """
                SELECT actor_id, name, bio, birthdate, photo_url
                FROM ACTOR
                WHERE actor_id = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, actorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date bd = rs.getDate("birthdate");
                return new Actor(
                        rs.getInt("actor_id"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        bd != null ? bd.toLocalDate() : null,
                        rs.getString("photo_url")
                );
            }

        } catch (SQLException e) {
            System.out.println("ActorDAO.getActorById error: " + e.getMessage());
        }

        return null;
    }

    // Query 17b — full filmography for an actor, ordered newest first
    public List<ActorFilmEntry> getFilmography(int actorId) {
        List<ActorFilmEntry> films = new ArrayList<>();

        String sql = """
                SELECT m.movie_id, m.title, m.release_year, m.poster_url, ma.character_name
                FROM MOVIE_ACTOR ma
                JOIN MOVIE m ON ma.movie_id = m.movie_id
                WHERE ma.actor_id = ?
                ORDER BY m.release_year DESC
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, actorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int year = rs.getInt("release_year");
                films.add(new ActorFilmEntry(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.wasNull() ? null : year,
                        rs.getString("poster_url"),
                        rs.getString("character_name")
                ));
            }

        } catch (SQLException e) {
            System.out.println("ActorDAO.getFilmography error: " + e.getMessage());
        }

        return films;
    }
}
