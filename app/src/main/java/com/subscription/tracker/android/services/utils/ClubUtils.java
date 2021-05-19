package com.subscription.tracker.android.services.utils;

import com.subscription.tracker.android.entity.response.Club;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserClub;

public class ClubUtils {

    private ClubUtils() {}

    public static Club getActiveClub(final SingleUserData singleUserData) {
        for (UserClub userClub : singleUserData.getUserClubs()) {
            if (userClub.isActiveClub()) {
                return userClub.getClub();
            }
        }
        return null;
    }

    public static UserClub getActiveUserClub(final SingleUserData singleUserData) {
        for (UserClub userClub : singleUserData.getUserClubs()) {
            if (userClub.isActiveClub()) {
                return userClub;
            }
        }
        return null;
    }

}
