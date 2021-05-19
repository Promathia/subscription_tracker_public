package com.subscription.tracker.android.activity.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.subscription.tracker.android.R;

import static com.subscription.tracker.android.Constants.MOBILE_PARAM;

public class PhoneLoginActivity extends AppCompatActivity {

    private EditText editTextMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        editTextMobile = findViewById(R.id.phone_login_edit_text_mobile);
        findViewById(R.id.phone_login_button_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = editTextMobile.getText().toString().trim();
                if (mobile.length() != 13) {
                    editTextMobile.setError(getString(R.string.phone_number_invalid));
                    editTextMobile.requestFocus();
                    return;
                }
                Intent intent = new Intent(PhoneLoginActivity.this, VerifyPhoneActivity.class);
                intent.putExtra(MOBILE_PARAM, mobile);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
