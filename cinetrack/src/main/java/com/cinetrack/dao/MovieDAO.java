package com.cinetrack.dao;

import com.cinetrack.model.Movie;
import com.cinetrack.model.MovieCastEntry;
import com.cinetrack.model.MovieDirectorEntry;
import com.cinetrack.model.MovieReview;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class MovieDAO {

    private final DataSource dataSource;

    public MovieDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*
    Query 1 - full movie information.
    */
   public Movie getMovieById(int movieId) {
    String sql = """
            SELECT movie_id, title, release_year, runtime_min, synopsis, poster_url, avg_rating
            FROM MOVIE
            WHERE movie_id = ?
            """;

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, movieId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) { 
                    double avg = rs.getDouble("avg_rating");
                    return new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getObject("release_year", Integer.class),
                        rs.getObject("runtime_min", Integer.class),
                        rs.getString("synopsis"),
                        rs.getString("poster_url"),
                        rs.wasNull() ? null : avg
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
        Query 2 = Genres for a Movie
        */
       public List<String> getGenres(int movieId) { 
        List<String> genres = new ArrayList<>();
        String sql = """
                SELECT g.name
                FROM MOVIE_GENRE mg
                JOIN GENRE g ON mg.genre_id = g.genre_id
                WHERE mg.movie_id = ?
                ORDER BY g.name
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                genres.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }


     /*
     Query 3 - Director for a Movie
     */
    public List<MovieDirectorEntry> getDirectors(int movieId) {
        List<MovieDirectorEntry> directors = new ArrayList<>();
        String sql = """
                SELECT d.director_id, d.name, d.photo_url
                FROM MOVIE_DIRECTOR md
                JOIN DIRECTOR d ON md.director_id = d.director_id
                WHERE md.movie_id = ?
                ORDER BY d.name
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                directors.add(new MovieDirectorEntry(
                        rs.getInt("director_id"),
                        rs.getString("name"),
                        rs.getString("photo_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return directors;
    }

    /*
    Query 4 - Cast for a Movie
    */
   public List<MovieCastEntry> getCast(int movieId) {
       List<MovieCastEntry> cast = new ArrayList<>();
       String sql = """
               SELECT a.actor_id, a.name, ma.character_name, a.photo_url
               FROM MOVIE_ACTOR ma
               JOIN ACTOR a ON ma.actor_id = a.actor_id
               WHERE ma.movie_id = ?
               ORDER BY a.name
               """;

       try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

           stmt.setInt(1, movieId);
           ResultSet rs = stmt.executeQuery();

           while (rs.next()) {
               cast.add(new MovieCastEntry(
                       rs.getInt("actor_id"),
                       rs.getString("name"),
                       rs.getString("character_name"),
                       rs.getString("photo_url")
               ));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return cast;
   }

   /*
   Query 5 - Reviews for a Movie
   */
   public List<MovieReview> getReviews(int movieId) {
       List<MovieReview> reviews = new ArrayList<>();
       String sql = """
               SELECT r.review_id, u.username, r.user_id, r.rating, r.body, r.created_at
               FROM REVIEW r
               JOIN USER u ON r.user_id = u.user_id
               WHERE r.movie_id = ?
               ORDER BY r.created_at DESC
               """;

       try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

           stmt.setInt(1, movieId);
           ResultSet rs = stmt.executeQuery();

           while (rs.next()) {
               reviews.add(new MovieReview(
                       rs.getInt("review_id"),
                       rs.getString("username"),
                       rs.getInt("user_id"),
                       rs.getInt("rating"),
                       rs.getString("body"),
                       rs.getTimestamp("created_at").toLocalDateTime()
               ));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return reviews;
   }

   /*
   Query 6 - Rating + Review Count 
   */
  public double[] getRatingStat(int movieId) {
    double[] stats = new double[2]; // [averageRating, reviewCount]
    String sql = """
            SELECT AVG(rating) AS average_rating, COUNT(*) AS review_count
            FROM REVIEW
            WHERE movie_id = ?
            """;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, movieId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            stats[0] = rs.getDouble("average_rating");
            stats[1] = rs.getInt("review_count");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return stats;
  }

  public MovieReview getUserReview(int userId, int movieId) {
      String sql = """
              SELECT r.review_id, u.username, r.user_id, r.rating, r.body, r.created_at
              FROM REVIEW r
              JOIN USER u ON r.user_id = u.user_id
              WHERE r.user_id = ? AND r.movie_id = ?
              """;
      try (Connection conn = dataSource.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setInt(1, userId);
          stmt.setInt(2, movieId);
          ResultSet rs = stmt.executeQuery();
          if (rs.next()) {
              return new MovieReview(
                      rs.getInt("review_id"),
                      rs.getString("username"),
                      rs.getInt("user_id"),
                      rs.getInt("rating"),
                      rs.getString("body"),
                      rs.getTimestamp("created_at").toLocalDateTime()
              );
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return null;
  }

  public void upsertReview(int userId, int movieId, int rating, String body) {
      String sql = """
              INSERT INTO REVIEW (user_id, movie_id, rating, body)
              VALUES (?, ?, ?, ?)
              ON DUPLICATE KEY UPDATE rating = VALUES(rating), body = VALUES(body)
              """;
      try (Connection conn = dataSource.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setInt(1, userId);
          stmt.setInt(2, movieId);
          stmt.setInt(3, rating);
          stmt.setString(4, body != null ? body : "");
          stmt.executeUpdate();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

}
