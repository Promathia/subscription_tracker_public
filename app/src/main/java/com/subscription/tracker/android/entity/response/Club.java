package com.subscription.tracker.android.entity.response;

import java.io.Serializable;

public class Club implements Serializable {

    private int id;
    private String clubName;
    private String clubNameAlt;
    private String imageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubNameAlt() {
        return clubNameAlt;
    }

    public void setClubNameAlt(String clubNameAlt) {
        this.clubNameAlt = clubNameAlt;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
