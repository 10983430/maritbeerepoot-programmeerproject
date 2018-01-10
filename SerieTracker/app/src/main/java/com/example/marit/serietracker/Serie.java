package com.example.marit.serietracker;

import java.util.ArrayList;

/**
 * Contains the information about series
 */

public class Serie {
    private String name;
    private String year;
    private String imdbrating;
    private Integer votesrating;
    private Integer numberOfSeasons;
    private String plot;
    private  String urlImage;
    private String awards;
    private ArrayList<Object> listOfEpisodes;


    /**
     * Constructs the class
     */
    public Serie(String name, String year, String imdbrating, Integer votesrating, Integer numberOfSeasons, String plot, String urlImage, String awards, ArrayList listOfEpisodes){
        this.name = name;
        this.year = year;
        this.imdbrating = imdbrating;
        this.votesrating = votesrating;
        this.numberOfSeasons = numberOfSeasons;
        this.plot = plot;
        this.urlImage = urlImage;
        this.awards = awards;
        this.listOfEpisodes = listOfEpisodes;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getImdbrating() {
        return imdbrating;
    }

    public Integer getVotesrating() {
        return votesrating;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public String getPlot() {
        return plot;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getAwards() {
        return awards;
    }

    public ArrayList getListOfEpisodes() {
        return listOfEpisodes;
    }
}
