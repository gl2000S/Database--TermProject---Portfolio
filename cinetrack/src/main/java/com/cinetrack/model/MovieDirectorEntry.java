package com.cinetrack.model;

public class MovieDirectorEntry {
    private int directorId;
    private String name;
    private String photoUrl;

    public MovieDirectorEntry(int directorId, String name, String photoUrl) {

        this.directorId = directorId;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public int getDirectorId() {
        return directorId;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
