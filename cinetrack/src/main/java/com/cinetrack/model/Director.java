package com.cinetrack.model;

import java.time.LocalDate;

public class Director {
    private int directorId;
    private String name;
    private String bio;
    private LocalDate birthdate;
    private String photoUrl;

    public Director() {}

    public Director(int directorId, String name, String bio, LocalDate birthdate, String photoUrl) {
        this.directorId = directorId;
        this.name = name;
        this.bio = bio;
        this.birthdate = birthdate;
        this.photoUrl = photoUrl;
    }

    public int getDirectorId()     { return directorId; }
    public String getName()        { return name; }
    public String getBio()         { return bio; }
    public LocalDate getBirthdate(){ return birthdate; }
    public String getPhotoUrl()    { return photoUrl; }
}
