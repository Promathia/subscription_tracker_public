package com.subscription.tracker.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.subscription.tracker.android.Constants;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.authentication.AfterSignInActivity;
import com.subscription.tracker.android.activity.authentication.PhoneLoginActivity;
import com.subscription.tracker.android.entity.response.RegistrationType;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.AuthenticationService;

import static com.subscription.tracker.android.Constants.GOOGLE_SIGN_IN_ACTIVITY_CODE;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private ImageButton googleSignIn;
    private ImageButton phoneSignIn;
    private AuthenticationService authenticationService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.authenticationService = new AuthenticationService();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            final Intent intent = new Intent(this, AfterSignInActivity.class);
            intent.putExtra(Constants.USER_DTO_EXTRA_PARAM, authenticationService.getUserDTOFromFirebase(currentUser));
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_start);
            this.googleSignIn = findViewById(R.id.google_sign_in_button);
            this.googleSignIn.setOnClickListener(this);
            this.phoneSignIn = findViewById(R.id.phone_sign_in_button);
            this.phoneSignIn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                handleGoogleLogin();
                break;
            case R.id.phone_sign_in_button:
                Intent intent = new Intent(this, PhoneLoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void handleGoogleLogin() {
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        final Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_ACTIVITY_CODE);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_ACTIVITY_CODE) {
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),getString(R.string.error_google_auth_failed),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final String idToken) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final Intent intent = new Intent(StartActivity.this, AfterSignInActivity.class);
                            intent.putExtra(Constants.USER_DTO_EXTRA_PARAM, authenticationService.getUserDTOFromFirebase(task.getResult().getUser()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),getString(R.string.error_auth_failed),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
