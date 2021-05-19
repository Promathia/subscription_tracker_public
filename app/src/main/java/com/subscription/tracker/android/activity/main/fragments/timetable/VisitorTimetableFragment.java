package com.subscription.tracker.android.activity.main.fragments.timetable;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.ClubService;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.utils.ClubUtils;

public class VisitorTimetableFragment extends Fragment {

    private TextView mPreview;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private SingleUserData singleUserResponse;
    private TimeTableViewHolder viewHolder;

    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_timetable_visitor, container, false);
        this.mPreview = root.findViewById(R.id.timetable_text_visitor_view);
        this.progressBar = root.findViewById(R.id.timetable_visitor_progress_bar);
        this.scrollView = root.findViewById(R.id.timetable_visitor_main_content_layout);
        this.singleUserResponse = new SharedPreferencesService(getContext())
                .getObject(R.string.preference_user_data_key, SingleUserData.class);
        this.viewHolder = new TimeTableViewHolder(null, null, mPreview, progressBar, scrollView);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ClubService clubService = new ClubService(getContext());
        clubService.setTimeTableText(viewHolder, ClubUtils.getActiveClub(singleUserResponse).getId(), true);
    }

}