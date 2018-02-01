package com.example.marit.serietrackerapplication;
//TODO of alle info laten zien in serieDetails of hier de onnodige weg halen
/**
 * Contains all the information about a Serie
 */
public class Serie {
    private String title;
    private String released;
    private String plot;
    private String awards;
    private String poster;
    private String imdbrating;
    private String imdbvotes;
    private String totalSeasons;

    public Serie() {

    }

    public String getTitle() {
        return title;
    }

    public String getReleased() {
        return released;
    }


    public String getPlot() {
        return plot;
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

    public String getTotalSeasons() {
        return totalSeasons;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setImdbrating(String imdbrating) {
        this.imdbrating = imdbrating;
    }

    public void setImdbvotes(String imdbvotes) {
        this.imdbvotes = imdbvotes;
    }

    public void setTotalSeasons(String totalSeasons) {
        this.totalSeasons = totalSeasons;
    }
}
