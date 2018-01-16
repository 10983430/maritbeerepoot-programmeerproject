package com.example.marit.serietrackerapplication;

/**
 * Created by Marit on 16-1-2018.
 */

public class Season {
    private String title;
    private String year;
    private String released;
    private String runtime;
    private String director;
    private String writer;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String poster;
    private double imdbrating;
    private String imdbvotes;
    private Integer totalSeasons;

    public Season(String title, String year, String released, String runtime, String director, String writer, String plot, String language, String country, String awards, String poster, double imdbrating, String imdbvotes, Integer totalSeasons) {
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.director = director;
        this.writer = writer;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.poster = poster;
        this.imdbrating = imdbrating;
        this.imdbvotes = imdbvotes;
        this.totalSeasons = totalSeasons;
    }

    public String getTitle() {
        return title;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getDirector() {
        return director;
    }

    public String getWriter() {
        return writer;
    }

    public String getPlot() {
        return plot;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getAwards() {
        return awards;
    }

    public String getPoster() {
        return poster;
    }

    public double getImdbrating() {
        return imdbrating;
    }

    public String getImdbvotes() {
        return imdbvotes;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }
}
