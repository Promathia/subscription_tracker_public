package com.subscription.tracker.android.activity.main.fragments.clubinfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.ClubService;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.utils.ClubUtils;
import com.subscription.tracker.android.services.utils.HtmlUtils;

import jp.wasabeef.richeditor.RichEditor;

public class VisitorClubInfoFragment extends Fragment {

    private TextView mPreview;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private SingleUserData singleUserResponse;
    private ClubInfoViewHolder viewHolder;

    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_club_info_visitor, container, false);
        this.mPreview = root.findViewById(R.id.club_info_text_visitor_view);
        this.mPreview.setMovementMethod(LinkMovementMethod.getInstance());
        this.progressBar = root.findViewById(R.id.club_info_progress_bar_visitor);
        this.scrollView = root.findViewById(R.id.club_info_main_content_layout_visitor);
        this.singleUserResponse = new SharedPreferencesService(getContext())
                .getObject(R.string.preference_user_data_key, SingleUserData.class);
        this.viewHolder = new ClubInfoViewHolder(null, null, mPreview, progressBar, scrollView);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ClubService clubService = new ClubService(getContext());
        clubService.setClubInfoText(viewHolder, ClubUtils.getActiveClub(singleUserResponse).getId(), true);
    }
}