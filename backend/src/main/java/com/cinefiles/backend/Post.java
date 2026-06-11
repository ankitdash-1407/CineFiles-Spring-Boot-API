package com.cinefiles.backend;
import java.sql.Timestamp;

public class Post {
    private int postId;
    private String authorName;
    private String movieTitle;
    private String text;
    private String mediaUrl;
    private Timestamp createdAt;
    private int likeCount; // <-- NEW: The Like Counter

    // Add likeCount to the bouncer!
    public Post(int postId, String authorName, String movieTitle, String text, String mediaUrl, Timestamp createdAt, int likeCount) {
        this.postId = postId;
        this.authorName = authorName;
        this.movieTitle = movieTitle;
        this.text = text;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }

    public int getPostId() { return postId; }
    public String getAuthorName() { return authorName; }
    public String getMovieTitle() { return movieTitle; }
    public String getText() { return text; }
    public String getMediaUrl() { return mediaUrl; }
    public Timestamp getCreatedAt() { return createdAt; }
    public int getLikeCount() { return likeCount; } // <-- NEW Getter
}
