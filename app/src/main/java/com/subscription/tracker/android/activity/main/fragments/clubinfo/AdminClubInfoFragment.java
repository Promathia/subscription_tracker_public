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

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.dialogs.AddHyperlinkDialog;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.ClubService;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.utils.ClubUtils;
import com.subscription.tracker.android.services.utils.HtmlUtils;

import jp.wasabeef.richeditor.RichEditor;

public class AdminClubInfoFragment extends Fragment {

    private RichEditor mEditor;
    private TextView mPreview;
    private TextView mClubInfoId;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private Button saveButton;
    private SingleUserData singleUserResponse;
    private ClubInfoViewHolder viewHolder;

    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_club_info_admin, container, false);
        this.mEditor = root.findViewById(R.id.club_info_admin_rte);
        this.mEditor.setEditorHeight(200);
        this.mEditor.setPadding(20, 20, 20, 20);
        this.mEditor.setEditorFontSize(18);
        this.mEditor.setEditorFontColor(R.color.colorPrimary);
        this.mEditor.setPlaceholder(getString(R.string.timetable_insert_text_hint));
        this.mPreview = root.findViewById(R.id.club_info_text_admin_view);
        this.mPreview.setMovementMethod(LinkMovementMethod.getInstance());
        ;
        this.mClubInfoId = root.findViewById(R.id.club_info_id);
        this.progressBar = root.findViewById(R.id.club_info_progress_bar);
        this.scrollView = root.findViewById(R.id.club_info_main_content_layout);
        setEditorOnClickListeners(root);
        this.singleUserResponse = new SharedPreferencesService(getContext())
                .getObject(R.string.preference_user_data_key, SingleUserData.class);
        this.viewHolder = new ClubInfoViewHolder(mEditor, mClubInfoId, mPreview, progressBar, scrollView);
        this.saveButton = root.findViewById(R.id.club_info_submit_text_button);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ClubService clubService = new ClubService(getContext());
        clubService.setClubInfoText(viewHolder, ClubUtils.getActiveClub(singleUserResponse).getId(), false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String html = mEditor.getHtml();
                if (TextUtils.isEmpty(mClubInfoId.getText())) {
                    clubService.createNewInformation(viewHolder,
                            ClubUtils.getActiveClub(singleUserResponse).getId(), html);
                } else {
                    clubService.updateExistingInformation(viewHolder,
                            ClubUtils.getActiveClub(singleUserResponse).getId(), mClubInfoId.getText(), html);
                }
            }
        });
    }

    private void setEditorOnClickListeners(final View root) {
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mPreview.setText(HtmlUtils.getSpannedFromHtmlWithAPI(text));
            }
        });

        root.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        root.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        root.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        root.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        root.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        root.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        root.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        root.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        root.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        root.findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        root.findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        root.findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        root.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHyperlinkDialog();
            }
        });

        root.findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        root.findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        root.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        root.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        root.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        root.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        root.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

    }

    private void showAddHyperlinkDialog() {
        final FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        final Fragment prev = this.getFragmentManager().findFragmentByTag("addHyperlinkDialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        final DialogFragment dialogFragment = new AddHyperlinkDialog(mEditor);
        dialogFragment.show(fragmentTransaction, "addHyperlinkDialog");
    }
}