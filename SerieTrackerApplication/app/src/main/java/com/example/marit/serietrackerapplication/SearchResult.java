package com.example.marit.serietrackerapplication;

/**
 * Stores the basic information about a searchresult
 */

public class SearchResult {
    private String title;
    private String url;
    private String imdbID;

    public SearchResult(String title, String url, String imdbID) {
        this.title = title;
        this.url = url;
        this.imdbID = imdbID;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImdbID() {
        return imdbID;
    }
}

