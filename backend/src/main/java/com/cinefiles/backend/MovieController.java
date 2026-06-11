package com.cinefiles.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @GetMapping("/search")
    public Object searchForMovie(@RequestParam String title) {

        // IMPORTANT: Check your ApiManager.java file!
        // If your method is named something else (like getMovie or fetchMovieData),
        // change "searchMovie" to match it exactly.
        return ApiManager.fetchAndCacheMovie(title);
    }
}
