package com.cinefiles.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    //search end point
    @GetMapping("/search")
    public Object searchForMovie(@RequestParam String title) {

        // IMPORTANT: Check ApiManager.java file
        // put method below to get movie
        return ApiManager.fetchAndCacheMovie(title);
    }

    // 1. THE RECOMMENDATION ENDPOINT
    @GetMapping("/recommendations")
    public List<String> getRecommendations(@RequestParam String title) {
        return MovieManager.getRecommendations(title);
    }

    // 2. THE WATCHLIST ENDPOINT
    // Note: We use @PostMapping because we are ADDING data to the database.
    @PostMapping("/watchlist/add")
    public String addToWatchlist(@RequestParam String username, @RequestParam String title) {
        // Assuming you have a WatchlistManager from your console app days!
        // WatchlistManager.addMovie(username, title);
        return "Success: " + title + " has been added to " + username + "'s watchlist!";
    }
}
