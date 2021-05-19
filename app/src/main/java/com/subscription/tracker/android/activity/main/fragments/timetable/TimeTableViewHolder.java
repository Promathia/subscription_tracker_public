package com.subscription.tracker.android.activity.main.fragments.timetable;

import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import jp.wasabeef.richeditor.RichEditor;

public class TimeTableViewHolder {

    private RichEditor mEditor;
    private TextView mTimetableId;
    private TextView mPreview;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    public TimeTableViewHolder(RichEditor mEditor,
                               TextView mTimetableId,
                               TextView mPreview,
                               ProgressBar progressBar,
                               ScrollView scrollView) {
        this.mEditor = mEditor;
        this.mTimetableId = mTimetableId;
        this.mPreview = mPreview;
        this.progressBar = progressBar;
        this.scrollView = scrollView;
    }

    public RichEditor getmEditor() {
        return mEditor;
    }

    public TextView getmTimetableId() {
        return mTimetableId;
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
