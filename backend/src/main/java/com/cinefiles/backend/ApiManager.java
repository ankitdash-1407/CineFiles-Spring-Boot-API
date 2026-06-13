package com.cinefiles.backend;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiManager {

    // need to get a free API key from omdbapi.com
    // Instead of: private static final String API_KEY = "12345abc";
    private static final String API_KEY = System.getenv("OMDB_API_KEY");

    // 1. The Promise: Change 'void' to 'Movie'
    public static Movie fetchAndCacheMovie(String title) {
        System.out.println("[🌐 CACHE MISS] Movie not found locally. Connecting to OMDb API...");

        String formattedTitle = title.replace(" ", "+");
        String url = "http://www.omdbapi.com/?t=" + formattedTitle + "&apikey=" + API_KEY;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            // 2. If the API fails to find it, return an empty hand (null)
            if (jsonResponse.contains("\"Response\":\"False\"")) {
                return null;
            }

            // Extract the groceries
            String fetchedTitle = extractJsonValue(jsonResponse, "Title");
            String fetchedGenre = extractJsonValue(jsonResponse, "Genre");
            String imdbRatingStr = extractJsonValue(jsonResponse, "imdbRating");
            double rating = imdbRatingStr.equals("N/A") ? 0.0 : Double.parseDouble(imdbRatingStr);

            // 3. THE CACHE: Save it to the database behind the scenes
            MovieManager.addMovie(fetchedTitle, fetchedGenre, rating);
            int fetchedId = MovieManager.getMovieId(fetchedTitle);

            // 4. THE BUCKET: Pack the groceries into the Movie object
            Movie internetMovieBucket = new Movie(fetchedId,fetchedTitle, fetchedGenre, rating);

            // 5. THE DELIVERY: Hand the bucket back to the Main Engine
            return internetMovieBucket;

        } catch (Exception e) {
            System.err.println("[CRITICAL] Internet connection failed: " + e.getMessage());
            return null; // Return empty hand if the internet breaks
        }
    }

    // A helper method to extract values from JSON without needing external libraries
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey) + searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (startIndex < searchKey.length() || endIndex == -1) return "N/A";
        return json.substring(startIndex, endIndex);
    }
}
