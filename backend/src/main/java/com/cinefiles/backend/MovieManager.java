package com.cinefiles.backend;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovieManager {

    // The method to push a new movie across the bridge
    public static void addMovie(String title, String genre, double rating) {

        // 1. THE BLUEPRINT
        // Hint: The table is named 'Movies'. The columns are title, attribute, and rating.
        String sql = "INSERT INTO Movies (title, attribute, rating) VALUES (?,?,?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 2. THE ENVELOPE
            // Hint: You have 3 question marks in your blueprint. 
            pstmt.setString(1, title);
            pstmt.setString(2, genre);
            pstmt.setDouble(3, rating);

            // 3. SEND IT ACROSS THE BRIDGE
            pstmt.executeUpdate();
            System.out.println("[SUCCESS] Movie Added to Vault: " + title);

        } catch (SQLException e) {
            System.err.println("[FAILED] Could not add movie.");
            System.err.println("Reason: " + e.getMessage());
        }
    }

    // --- THE TRANSLATOR: Get Movie ID ---
    public static int getMovieId(String title) {
        String sql = "SELECT movie_id FROM Movies WHERE title = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("movie_id"); // Found the ID!
            }
        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not fetch Movie ID: " + e.getMessage());
        }
        return -1; // -1 means the movie does not exist
    }

    // --- THE CACHE CHECK: Look in local database first ---
    public static Movie searchLocalDatabase(String title) {
        String sql = "SELECT * FROM Movies WHERE title = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            java.sql.ResultSet rs = pstmt.executeQuery();


            // If rs.next() is true, the movie exists in our Vault!
            if (rs.next()) {
                // Step A: Extract the loose groceries from MySQL
                int fetchedId = rs.getInt("movie_id");
                String fetchedTitle = rs.getString("title");
                String fetchedGenre = rs.getString("attribute");
                double fetchedRating = rs.getDouble("rating");

                // Step B: Pack the groceries into the Bucket
                Movie myMovieBucket = new Movie( fetchedId,fetchedTitle, fetchedGenre, fetchedRating);

                // Step C: Hand the bucket back to the main engine
                return myMovieBucket;
            }
        } catch (SQLException e) {
            System.err.println("[CRITICAL] Search failed: " + e.getMessage());
        }

        // 3. If movie was not found, return an empty hand (null) instead of false
        return null;
    }
}