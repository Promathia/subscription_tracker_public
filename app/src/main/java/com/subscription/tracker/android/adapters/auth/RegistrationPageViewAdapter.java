package com.subscription.tracker.android.adapters.auth;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.subscription.tracker.android.activity.authentication.AfterSignInActivity;
import com.subscription.tracker.android.activity.authentication.fragments.UserRegistrationClub;
import com.subscription.tracker.android.activity.authentication.fragments.UserRegistrationPersonalData;
import com.subscription.tracker.android.entity.response.SingleUserData;

public class RegistrationPageViewAdapter extends FragmentStateAdapter {

    private SingleUserData singleUserData;
    private AfterSignInActivity activity;

    public RegistrationPageViewAdapter(AfterSignInActivity activity, SingleUserData singleUserData) {
        super(activity);
        this.singleUserData = singleUserData;
        this.activity = activity;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserRegistrationClub(singleUserData, activity);
            case 1:
                return new UserRegistrationPersonalData(singleUserData);
            default:
                return new UserRegistrationClub(singleUserData, activity);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
