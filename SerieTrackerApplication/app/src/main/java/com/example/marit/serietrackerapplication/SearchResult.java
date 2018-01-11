package com.example.marit.serietrackerapplication;

/**
 * Created by Marit on 11-1-2018.
 */

public class SearchResult {
    private String title;
    private String url;
    private String imdbID;
    private String year;

    public SearchResult (String title, String url, String imdbID, String year) {
        this.title = title;
        this.url = url;
        this.imdbID = imdbID;
        this.year = year;
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

    public String getYear() {
        return year;
    }
}

