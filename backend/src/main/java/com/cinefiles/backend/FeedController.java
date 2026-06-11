package com.cinefiles.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

// 1. @RestController tells Spring Boot: "This class talks to the internet!"
@RestController
@RequestMapping("/api")
public class FeedController {

    // 2. @GetMapping tells Spring Boot: "If a browser asks for /api/feed, run this method."
    @GetMapping("/feed")
    public List<Post> serveGlobalFeed() {

        // 3. We ask your original Manager for the data. Spring Boot AUTOMATICALLY converts it to JSON!
        return PostManager.getGlobalFeed();
    }
}
