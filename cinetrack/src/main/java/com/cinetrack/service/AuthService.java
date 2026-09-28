package com.cinetrack.service;

import com.cinetrack.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class AuthService {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean registerUser(String username, String email, String password, String bio) {
        String sql = """
                INSERT INTO `USER` (username, email, password_hash, bio)
                VALUES (?, ?, ?, ?)
                """;

        String hashedPassword = passwordEncoder.encode(password);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, bio);

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String usernameOrEmail, String password) {
        String sql = """
                SELECT user_id, username, email, password_hash, bio, created_at
                FROM `USER`
                WHERE username = ? OR email = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                if (passwordEncoder.matches(password, storedHash)) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            storedHash,
                            rs.getString("bio"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }
}
