package com.cinetrack.model;

import java.time.LocalDate;

public class Actor {
    private int actorId;
    private String name;
    private String bio;
    private LocalDate birthdate;
    private String photoUrl;

    public Actor() {}

    public Actor(int actorId, String name, String bio, LocalDate birthdate, String photoUrl) {
        this.actorId = actorId;
        this.name = name;
        this.bio = bio;
        this.birthdate = birthdate;
        this.photoUrl = photoUrl;
    }

    public int getActorId()        { return actorId; }
    public String getName()        { return name; }
    public String getBio()         { return bio; }
    public LocalDate getBirthdate(){ return birthdate; }
    public String getPhotoUrl()    { return photoUrl; }
}
