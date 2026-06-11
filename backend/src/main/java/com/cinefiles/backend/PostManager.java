package com.cinefiles.backend;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostManager {

    // 1. The Promise: Accept the author's bucket, text, media, and an optional movie ID.
    public static boolean createPost(User author, String postText, String mediaUrl, int movieId) {

        // 2. The Blueprint: We omit post_id and created_at because MySQL generates those automatically.
        String sql = "INSERT INTO posts (user_id, post_text, media_url, movie_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Fill the Envelope
            pstmt.setInt(1, author.getId()); // Extract the exact ID from the bucket!
            pstmt.setString(2, postText);

            // Handle optional media URL
            if (mediaUrl.isEmpty()) {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(3, mediaUrl);
            }

            //This is where we handle the crucial "Edge Case": checking if the user actually tagged a movie, or if they left it blank.
            //If they didn't tag a movie, our Engine will pass -1 to the Manager, and the Manager will tell MySQL to leave that column NULL.
            if (movieId > 0) {
                pstmt.setInt(4, movieId);
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER); // Tell MySQL to leave it empty
            }

            // 5. Send across the bridge
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not create post: " + e.getMessage());
            return false;
        }
    }

    // GLOBALFEED__
    public static List<Post> getGlobalFeed() {
        List<Post> feedCrate = new ArrayList<>();

        // THE NEW BLUEPRINT: Notice the tiny SELECT COUNT(*) hiding inside the main query!
        String sql = "SELECT p.post_id, u.username, m.title, p.post_text, p.media_url, p.created_at, " +
                "(SELECT COUNT(*) FROM post_likes WHERE post_id = p.post_id) AS like_count " +
                "FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN movies m ON p.movie_id = m.movie_id " +
                "ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int fetchedId = rs.getInt("post_id");
                String fetchedAuthor = rs.getString("username");
                String fetchedTitle = rs.getString("title");
                String fetchedText = rs.getString("post_text");
                String fetchedMedia = rs.getString("media_url");
                java.sql.Timestamp fetchedDate = rs.getTimestamp("created_at");

                // Extract the counted likes from MySQL
                int fetchedLikes = rs.getInt("like_count");

                // Pack the new bucket
                Post feedPost = new Post(fetchedId, fetchedAuthor, fetchedTitle, fetchedText, fetchedMedia, fetchedDate, fetchedLikes);

                feedCrate.add(feedPost);
            }

        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not load the global feed: " + e.getMessage());
        }

        return feedCrate;
    }

    public static boolean likePost(int userId, int postId){
        String sql = "INSERT INTO post_likes (user_id , post_id) VALUEs(?,?)";
        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);

            pstmt.executeUpdate();
            return true;
        }catch (SQLException e) {
            return false;
        }
    }

    // --- THE WRITER: Add a comment to a post ---
    public static boolean writeComment(int postId, int userId, String commentText) {
        String sql = "INSERT INTO post_comments (post_id, user_id, comment_text) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, commentText);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[FAILED] Could not post comment: " + e.getMessage());
            return false;
        }
    }

    // --- THE READER: Get all comments for a specific post ---
    public static List<Comment> getCommentsForPost(int postId) {
        List<Comment> commentCrate = new ArrayList<>();

        // Blueprint: Join with Users to get the author's name!
        String sql = "SELECT c.comment_id, u.username, c.comment_text, c.created_at " +
                "FROM post_comments c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ? " +
                "ORDER BY c.created_at ASC"; // Oldest first, like a real conversation

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("comment_id");
                String author = rs.getString("username");
                String text = rs.getString("comment_text");
                java.sql.Timestamp date = rs.getTimestamp("created_at");

                Comment c = new Comment(id, author, text, date);
                commentCrate.add(c);
            }

        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not load comments: " + e.getMessage());
        }

        return commentCrate;
    }
}
