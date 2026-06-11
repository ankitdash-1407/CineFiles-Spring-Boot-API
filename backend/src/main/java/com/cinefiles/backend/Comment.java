package com.cinefiles.backend;
import java.sql.Timestamp;

public class Comment {
    // 1. The Vault
    private int commentId;
    private String authorName;
    private String text;
    private Timestamp createdAt;

    // 2. The Constructor
    public Comment(int commentId, String authorName, String text, Timestamp createdAt) {
        this.commentId = commentId;
        this.authorName = authorName;
        this.text = text;
        this.createdAt = createdAt;
    }

    // 3. Getters
    public int getCommentId() { return commentId; }
    public String getAuthorName() { return authorName; }
    public String getText() { return text; }
    public Timestamp getCreatedAt() { return createdAt; }
}
