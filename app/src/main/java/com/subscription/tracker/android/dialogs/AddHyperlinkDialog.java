package com.subscription.tracker.android.dialogs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.subscription.tracker.android.R;

import jp.wasabeef.richeditor.RichEditor;

public class AddHyperlinkDialog extends DialogFragment {

    private RichEditor mEditor;
    private EditText link;
    private EditText copy;
    private Button saveButton;
    private AddHyperlinkDialog currentDialog;

    public AddHyperlinkDialog(final RichEditor mEditor) {
        this.mEditor = mEditor;
        this.currentDialog = this;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_hyperlink, container, false);
        this.link = view.findViewById(R.id.hyperlink_setup_link);
        this.copy = view.findViewById(R.id.hyperlink_setup_copy);
        this.saveButton = view.findViewById(R.id.hyperlink_setup_save_button);
        initializeSaveButton();
        return view;
    }

    private void initializeSaveButton() {
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFieldsCorrect = true;
                final String copyString = copy.getText().toString();
                final String linkString = link.getText().toString();
                if (TextUtils.isEmpty(copyString)) {
                    copy.setError(getString(R.string.hyperlink_field_required));
                    isFieldsCorrect = false;
                }
                if (TextUtils.isEmpty(linkString)) {
                    link.setError(getString(R.string.hyperlink_field_required));
                    isFieldsCorrect = false;
                } else if (!linkString.startsWith("http")
                        && !linkString.startsWith("tel:")
                        && !linkString.startsWith("mailto:")) {
                    link.setError(getString(R.string.hyperlink_link_field_bad_format));
                    isFieldsCorrect = false;
                }
                if (isFieldsCorrect) {
                    mEditor.insertLink(linkString, copyString);
                    currentDialog.dismiss();
                }
            }
        });
    }

}
