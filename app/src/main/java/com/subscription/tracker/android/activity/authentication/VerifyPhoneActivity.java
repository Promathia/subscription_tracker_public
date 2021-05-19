package com.subscription.tracker.android.activity.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.subscription.tracker.android.Constants;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.services.AuthenticationService;

import java.util.concurrent.TimeUnit;

import static com.subscription.tracker.android.Constants.MOBILE_PARAM;

public class VerifyPhoneActivity extends AppCompatActivity {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCodeSendCallbacks;
    //The edittext to input the code
    private EditText editTextCode;
    private Button resendButton;
    private ProgressBar verifyProgress;
    //firebase auth object
    private FirebaseAuth mAuth;
    private AuthenticationService authenticationService;
    private boolean mVerificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        this.authenticationService = new AuthenticationService();
        this.mAuth = FirebaseAuth.getInstance();
        this.editTextCode = findViewById(R.id.editTextCode);
        final String mobile = getIntent().getStringExtra(MOBILE_PARAM);
        this.verificationCodeSendCallbacks = getVerificationCodeSendCallbacks();
        sendVerificationCode(mobile, false);
        findViewById(R.id.verify_phone_button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.length() != 6) {
                    editTextCode.setError(getString(R.string.phone_verification_code_malformat));
                    editTextCode.requestFocus();
                    return;
                }
                signInWithPhoneAuthCredential(code);
            }
        });
        this.resendButton = findViewById(R.id.phone_login_button_resend_code);
        this.resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendButton.setVisibility(View.GONE);
                verifyProgress.setVisibility(View.VISIBLE);
                sendVerificationCode(mobile, true);
            }
        });
        this.verifyProgress = findViewById(R.id.verify_phone_progress_progressbar);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (!mVerificationInProgress) {
            sendVerificationCode(getIntent().getStringExtra(MOBILE_PARAM), false);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, this.mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(final String mobile, final boolean isResend) {
        final PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(mobile)                   // Phone number to verify
                .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                        // Activity (for callback binding)
                .setCallbacks(verificationCodeSendCallbacks);
        if (isResend) {
            builder.setForceResendingToken(mResendToken);
        }
        PhoneAuthProvider.verifyPhoneNumber(builder.build());
        this.mVerificationInProgress = true;
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getVerificationCodeSendCallbacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    editTextCode.setText(code);
                    signInWithPhoneAuthCredential(code);
                }
                mVerificationInProgress = false;
            }

            @Override
            public void onVerificationFailed(FirebaseException er) {
                if (er instanceof FirebaseAuthInvalidCredentialsException) {
                    editTextCode.setError(getString(R.string.phone_number_invalid));
                } else if (er instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(), getString(R.string.phone_number_quota_exceeded), Toast.LENGTH_LONG).show();
                }
                mVerificationInProgress = false;
                resendButton.setVisibility(View.VISIBLE);
                verifyProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String verificationCode, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationCode, forceResendingToken);
                mVerificationId = verificationCode;
                mResendToken = forceResendingToken;
                mVerificationInProgress = false;
            }
        };
    }

    private void signInWithPhoneAuthCredential(String code) {
        mAuth.signInWithCredential(PhoneAuthProvider.getCredential(mVerificationId, code))
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(VerifyPhoneActivity.this, AfterSignInActivity.class);
                            intent.putExtra(Constants.USER_DTO_EXTRA_PARAM, authenticationService.getUserDTOFromFirebase(task.getResult().getUser()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            String message = getString(R.string.error_auth_failed);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = getString(R.string.phone_verification_code_entered_invalid);
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            resendButton.setVisibility(View.VISIBLE);
                            verifyProgress.setVisibility(View.GONE);
                        }
                    }
                });
    }

}
