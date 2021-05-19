package com.subscription.tracker.android.activity.main.fragments.appinfo;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.subscription.tracker.android.R;

public class AppInfoFragment extends Fragment {

    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_app_info, container, false);
        final TextView contacts = root.findViewById(R.id.app_info_contacts);
        contacts.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView licence = root.findViewById(R.id.app_info_licence);
        licence.setMovementMethod(LinkMovementMethod.getInstance());
        return root;
    }
}