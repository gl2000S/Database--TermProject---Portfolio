package com.cinetrack.model;

public class MovieCastEntry {
    private int actorId;
    private String actorName;
    private String characterName;
    private String photoUrl;

    public MovieCastEntry(int actorId, String actorName, String characterName, String photoUrl) {

        this.actorId = actorId;
        this.actorName = actorName;
        this.characterName = characterName;
        this.photoUrl = photoUrl;
    }

    public int getActorId() {
        return actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getPhotoUrl() {
        return photoUrl != null ? photoUrl : "https://via.placeholder.com/text=?";
    }
}
