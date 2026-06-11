package com.cinefiles.backend;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.util.Base64;

public class UserManager {

    // --- THE SECURE HASHING ALGORITHM (SHA-256) JAVA'S built-in---
    private static String hashPassword(String plainText) {
        try {
            // 1. Summon the Java meat grinder
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 2. Throw the password in and turn the grinder
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));

            // 3. Convert the raw bytes into a readable string to save in MySQL
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            System.err.println("[CRITICAL] Hashing Engine Failed.");
            return null;
        }
    }

    // The method to push a new user across the bridge
    // Update the method to accept a password
    public static boolean registerUser(String username, String email, String plainTextPassword) {

        // 1. Hash the password IMMEDIATELY
        String securedHash = hashPassword(plainTextPassword);

        // 2. The Blueprint now includes the password_hash column
        String sql = "INSERT INTO Users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, securedHash); // We send the ground beef, NOT the steak!

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    // The method to check if a user exists and the password matches
    // 1. The Promise: Change 'boolean' to 'User'
    public static User verifyLogin(String inputUsername, String inputPassword) {

        // 2. The Updated Blueprint: Ask for ALL the groceries we need for the bucket!
        String sql = "SELECT user_id, username, email, password_hash FROM Users WHERE username = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inputUsername);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String savedHash = rs.getString("password_hash");
                String attemptHash = hashPassword(inputPassword);

                if (savedHash.equals(attemptHash)) {
                    // --- SUCCESS! ---
                    // Step A: Extract the data
                    int fetchedId = rs.getInt("user_id");
                    String fetchedUsername = rs.getString("username");
                    String fetchedEmail = rs.getString("email");

                    // Step B: Pack the User bucket (Make sure your User.java constructor matches this!)
                    User loggedInUserBucket = new User(fetchedId, fetchedUsername, fetchedEmail);

                    // Step C: Hand the bucket back to the Engine
                    return loggedInUserBucket;
                }
            }
        } catch (SQLException e) {
            // Keep error messages clean, but no UI messages!
            System.err.println("[CRITICAL] Login query failed: " + e.getMessage());
        }

        // 3. The Catch-All: If password fails, user isn't found, or database crashes, return empty hand.
        return null;
    }

    // --- THE TRANSLATOR: Get User ID ---
    public static int getUserId(String username) {
        String sql = "SELECT user_id FROM Users WHERE username = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id"); // Found the ID!
            }
        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not fetch User ID: " + e.getMessage());
        }
        return -1; // -1 means the user was not found
    }

    // --- ADVANCED SQL: ACID TRANSACTION ---
    public static boolean deleteAccount(int userId) {
        // set up SQL commands in the exact order needed to prevent Foreign Key errors
        String deleteWatchlist = "DELETE FROM watchlists WHERE user_id = ?";
        String deleteLikes = "DELETE FROM post_likes WHERE user_id = ?";
        String deleteComments = "DELETE FROM post_comments WHERE user_id = ?";
        String deletePosts = "DELETE FROM posts WHERE user_id = ?";
        String deleteUser = "DELETE FROM users WHERE user_id = ?";

        // connection outside the try-with-resources so we can use it in the catch block
        Connection conn = null;

        try {
            conn = DatabaseEngine.connect();

            // 1. TURN OFF AUTO-SAVE (This starts the Transaction)
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteWatchlist);
                 PreparedStatement pstmt2 = conn.prepareStatement(deleteLikes);
                 PreparedStatement pstmt3 = conn.prepareStatement(deleteComments);
                 PreparedStatement pstmt4 = conn.prepareStatement(deletePosts);
                 PreparedStatement pstmt5 = conn.prepareStatement(deleteUser)) {

                // 2. Queue up all the commands
                pstmt1.setInt(1, userId); pstmt1.executeUpdate();
                pstmt2.setInt(1, userId); pstmt2.executeUpdate();
                pstmt3.setInt(1, userId); pstmt3.executeUpdate();
                pstmt4.setInt(1, userId); pstmt4.executeUpdate();
                pstmt5.setInt(1, userId); pstmt5.executeUpdate();

                // 3. IF WE SURVIVED THIS FAR: COMMIT EVERYTHING PERMANENTLY!
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            // 4. DISASTER STRIKES: ROLLBACK EVERYTHING!
            System.err.println("[CRITICAL] Transaction Failed. Rolling back changes: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback completely failed!");
                }
            }
            return false;
        } finally {
            // 5. Always clean up and turn auto-save back on for the next user
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

