package com.example.marit.serietrackerapplication;

/**
 * Created by Marit on 15-1-2018.
 */

public class Episode {
    private String title;
    private String released;
    private Integer episode;
    private String imdbrating;
    private String imdbid;
    private Integer seasonnumber;


    public Episode(String title, String released, Integer episode, String imdbrating, String imdbid, Integer seasonnnumber) {
        this.title = title;
        this.released = released;
        this.episode = episode;
        this.imdbrating = imdbrating;
        this.imdbid = imdbid;
        this.seasonnumber = seasonnnumber;
    }

    // TO-DO setters maken
    public Episode() {
        
    }

    public String getTitle() {
        return title;
    }

    public String getReleased() {
        return released;
    }

    public Integer getEpisode() {
        return episode;
    }

    public String getImdbrating() {
        return imdbrating;
    }

    public String getImdbid() {
        return imdbid;
    }

    public Integer getSeasonnumber() {
        return seasonnumber;
    }
}
