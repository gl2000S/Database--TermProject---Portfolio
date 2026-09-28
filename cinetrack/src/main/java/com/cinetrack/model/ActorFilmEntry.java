package com.cinetrack.model;

public class ActorFilmEntry {
    private int movieId;
    private String title;
    private Integer releaseYear;
    private String posterUrl;
    private String characterName;

    public ActorFilmEntry() {}

    public ActorFilmEntry(int movieId, String title, Integer releaseYear,
                          String posterUrl, String characterName) {
        this.movieId = movieId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.posterUrl = posterUrl;
        this.characterName = characterName;
    }

    public int getMovieId()          { return movieId; }
    public String getTitle()         { return title; }
    public Integer getReleaseYear()  { return releaseYear; }
    public String getPosterUrl()     { return posterUrl; }
    public String getCharacterName() { return characterName; }
}
