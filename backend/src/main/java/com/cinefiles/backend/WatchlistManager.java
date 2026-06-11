package com.cinefiles.backend;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WatchlistManager {

    // 1. The Promise: We accept our new User bucket and the movie title
    public static void addMovieToWatchlist(User currentUser, String movieTitle) {

        // 2. Extract the exact IDs we need for the MySQL Bridge
        int currentUserId = currentUser.getId();
        int targetMovieId = MovieManager.getMovieId(movieTitle);

        // Safety check: Did we actually find the movie in the database?
        if (targetMovieId == -1) {
            System.out.println("[FAILED] Could not find the movie ID in the database.");
            return;
        }

        // 3. The Blueprint: Insert into the Bridge Table
        String sql = "INSERT INTO Watchlists (user_id, movie_id) VALUES (?, ?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 4. Fill the envelope with the IDs
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, targetMovieId);

            // 5. Send it across the bridge!
            pstmt.executeUpdate();
            System.out.println("[SUCCESS] '" + movieTitle + "' was added to " + currentUser.getUsername() + "'s watchlist!");

        } catch (SQLException e) {
            // If your MySQL table has a Primary Key preventing duplicates,
            // it will throw an error if the user tries to add the same movie twice.
            System.err.println("[FAILED] Could not add to watchlist. You might already have it saved!");
        }
    }

    // --- THE VIEWER: Show a user's complete watchlist ---
    // 1. The Promise: We will return a List full of Movie buckets!
    public static List<Movie> viewWatchlist(User currentUser) {

        // 2. Create the empty crate to hold our buckets
        List<Movie> myWatchlistCrate = new ArrayList<>();

        // 3. The Blueprint: Notice we JOIN by user_id now, which is faster!
        String sql = "SELECT m.movie_id, m.title, m.attribute, m.rating " +
                "FROM Movies m " +
                "JOIN Watchlists w ON m.movie_id = w.movie_id " +
                "WHERE w.user_id = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Pass the ID from our User bucket
            pstmt.setInt(1, currentUser.getId());

            java.sql.ResultSet rs = pstmt.executeQuery();

            // Loop through every row the database found
            while (rs.next()) {
                // Step A: Extract the data
                int fetchedId = rs.getInt("movie_id");
                String fetchedTitle = rs.getString("title");
                String fetchedGenre = rs.getString("attribute");
                double fetchedRating = rs.getDouble("rating");

                // Step B: Pack it into a Movie Bucket
                Movie movieBucket = new Movie(fetchedId, fetchedTitle, fetchedGenre, fetchedRating);

                // Step C: Drop the bucket into the Crate
                myWatchlistCrate.add(movieBucket);
            }

        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not load watchlist: " + e.getMessage());
        }

        // 4. Return the crate (it might be empty if they have no movies, and that's okay!)
        return myWatchlistCrate;
    }
}