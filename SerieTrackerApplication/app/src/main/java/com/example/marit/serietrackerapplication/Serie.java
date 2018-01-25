package com.example.marit.serietrackerapplication;

/**
 * Created by Marit on 16-1-2018.
 */

public class Serie {
    private String title;
    private String year;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String poster;
    private String imdbrating;
    private String imdbvotes;
    private Integer totalSeasons;



    public Serie(String title, String year, String released, String runtime, String genre, String director, String writer, String plot, String language, String country, String awards, String poster, String imdbrating, String imdbvotes, Integer totalSeasons) {
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
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

    public String getYear() {
        return year;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
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

    public String getImdbrating() {
        return imdbrating;
    }

    public String getImdbvotes() {
        return imdbvotes;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }
}
