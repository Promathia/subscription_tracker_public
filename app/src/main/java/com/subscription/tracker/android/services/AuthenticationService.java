package com.subscription.tracker.android.services;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.subscription.tracker.android.entity.response.RegistrationType;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserForClubResponse;

public class AuthenticationService {

    public UserForClubResponse getUser(String userId, final Context context) {
        return new UserForClubResponse();
    }

    public SingleUserData getUserDTOFromFirebase(final FirebaseUser currentUser) {
        final SingleUserData singleUserData = new SingleUserData();
        singleUserData.setExternalId(currentUser.getUid());
        singleUserData.setEmail(currentUser.getEmail());
        singleUserData.setPhone(currentUser.getPhoneNumber());
        if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
            singleUserData.setRegistrationType(RegistrationType.Type.GOOGLE.getType());
        } else {
            singleUserData.setRegistrationType(RegistrationType.Type.PHONE.getType());
        }
        return singleUserData;
    }
}
