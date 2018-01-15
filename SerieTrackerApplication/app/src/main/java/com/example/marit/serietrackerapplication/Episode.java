package com.example.marit.serietrackerapplication;

/**
 * Created by Marit on 15-1-2018.
 */

public class Episode {
    private String title;
    private String released;
    private Integer episode;
    private double imdbrating;
    private String imdbid;


    public Episode(String title, String released, Integer episode, double imdbrating, String imdbid) {
        this.title = title;
        this.released = released;
        this.episode = episode;
        this.imdbrating = imdbrating;
        this.imdbid = imdbid;
    }
}
