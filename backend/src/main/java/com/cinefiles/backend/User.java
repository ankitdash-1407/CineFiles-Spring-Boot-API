package com.cinefiles.backend;
public class User {
    // 1. Private Variables (The Vault)
    // we removed the password (security!) and the ArrayList (database handles this now)
    private int id;
    private String username;
    private String email;

    // 2. Constructor (The Assembly Line)
    // We update this to perfectly match the 3 items we pass from UserManager
    public User(int inputId, String inputUsername, String inputEmail) {
        this.id = inputId;
        this.username = inputUsername;
        this.email = inputEmail;
    }

    // 3. Getters (Secure Windows)
    // No more action methods. Just getters to look inside the bucket.
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
