package com.example.marit.serietrackerapplication;

/**
 * Contains the general information about an episode
 */
public class Episode {
    private String title;
    private Integer episode;
    private String imdbid;
    private Integer seasonnumber;


    public Episode(String title, Integer episode, String imdbid, Integer seasonnnumber) {
        this.title = title;
        this.episode = episode;
        this.imdbid = imdbid;
        this.seasonnumber = seasonnnumber;
    }

    public Episode() {

    }

    public String getTitle() {
        return title;
    }

    public Integer getEpisode() {
        return episode;
    }

    public String getImdbid() {
        return imdbid;
    }

    public Integer getSeasonnumber() {
        return seasonnumber;
    }
}
