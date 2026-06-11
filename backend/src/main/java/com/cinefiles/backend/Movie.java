package com.cinefiles.backend;
public class Movie {
    // 1. Private Variables (The Vault)
    private int id; // <-- Added the Database ID
    private String title;
    private String genre;
    private double rating;

    // 2. Constructor (The Assembly Line)
    // Now expects 4 pieces of data!
    public Movie(int inputId, String inputTitle, String inputGenre, double inputRating) {
        this.id = inputId;
        this.title = inputTitle;
        this.genre = inputGenre;
        this.rating = inputRating;
    }

    // 3. Getters (The Secure Windows)
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }
}
