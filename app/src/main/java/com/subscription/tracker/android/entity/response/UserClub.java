package com.subscription.tracker.android.entity.response;

import java.io.Serializable;

public class UserClub implements Serializable {

    private Club club;
    private Role role;
    private boolean userAccepted;
    private boolean activeClub;

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public boolean isUserAccepted() {
        return userAccepted;
    }

    public void setUserAccepted(boolean userAccepted) {
        this.userAccepted = userAccepted;
    }

    public boolean isActiveClub() {
        return activeClub;
    }

    public void setActiveClub(boolean activeClub) {
        this.activeClub = activeClub;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
