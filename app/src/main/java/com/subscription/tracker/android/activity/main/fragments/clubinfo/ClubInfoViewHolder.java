package com.subscription.tracker.android.activity.main.fragments.clubinfo;

import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import jp.wasabeef.richeditor.RichEditor;

public class ClubInfoViewHolder {

    private RichEditor mEditor;
    private TextView mClubInfoId;
    private TextView mPreview;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    public ClubInfoViewHolder(RichEditor mEditor,
                              TextView mClubInfoId,
                              TextView mPreview,
                              ProgressBar progressBar,
                              ScrollView scrollView) {
        this.mEditor = mEditor;
        this.mClubInfoId = mClubInfoId;
        this.mPreview = mPreview;
        this.progressBar = progressBar;
        this.scrollView = scrollView;
    }

    public RichEditor getmEditor() {
        return mEditor;
    }

    public TextView getmClubInfoId() {
        return mClubInfoId;
    }

    public TextView getmPreview() {
        return mPreview;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public ScrollView getScrollView() {
        return scrollView;
    }
}
