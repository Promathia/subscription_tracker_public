package com.subscription.tracker.android.activity.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.Constants;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.main.AdminOwnerInstrActivity;
import com.subscription.tracker.android.adapters.auth.RegistrationPageViewAdapter;
import com.subscription.tracker.android.activity.main.VisitorActivity;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.RequestService;
import com.subscription.tracker.android.services.utils.RoleUtils;
import com.subscription.tracker.android.services.SharedPreferencesService;

import org.json.JSONObject;

import static com.subscription.tracker.android.Constants.API_URI;

public class AfterSignInActivity extends FragmentActivity {

    private ViewPager2 viewPager;

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_sign_in);
        SingleUserData singleUserData = (SingleUserData) getIntent().getSerializableExtra(Constants.USER_DTO_EXTRA_PARAM);
        RequestService.getInstance(
                this.getApplicationContext()).addToRequestQueue(prepareGetUserRequest(singleUserData)
        );
    }

    private JsonObjectRequest prepareGetUserRequest(final SingleUserData singleUserData) {
        String url = String.format("%s/users/%s", API_URI, singleUserData.getExternalId());
        return new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleUserFoundResponse(response, singleUserData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            handleNoUserResponse(singleUserData);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_getting_user_data_by_api), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleNoUserResponse(SingleUserData singleUserData) {
        initializeViewPager(0, singleUserData);
    }

    private void handleUserFoundResponse(JSONObject response, SingleUserData singleUserData) {
        try {
            SingleUserData singleUserResponse = new ObjectMapper().readValue(response.toString(), SingleUserData.class);
            if (singleUserResponse.getUserClubs().isEmpty()) {
                initializeViewPager(0, singleUserData);
            }
            if (!singleUserResponse.isFilled()) {
                initializeViewPager(1, singleUserData);
            }
            startActivityForUser(singleUserResponse);
        } catch (JsonProcessingException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_getting_user_data_by_api), Toast.LENGTH_LONG).show();
        }
    }

    private void startActivityForUser(SingleUserData singleUserResponse) {
        Intent intent;
        if (RoleUtils.isVisitor(singleUserResponse)) {
            intent = new Intent(this, VisitorActivity.class);
        } else {
            intent = new Intent(this, AdminOwnerInstrActivity.class);
        }
        new SharedPreferencesService(this).storeObject(R.string.preference_user_data_key, singleUserResponse);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initializeViewPager(final int position, SingleUserData singleUserData) {
        RegistrationPageViewAdapter adapter = new RegistrationPageViewAdapter(this, singleUserData);
        this.viewPager = findViewById(R.id.after_sign_in_view_pager);
        this.viewPager.setUserInputEnabled(false);
        this.viewPager.setAdapter(adapter);
        this.viewPager.setCurrentItem(position);
        ProgressBar progressBar = findViewById(R.id.after_sign_in_progress);
        progressBar.setVisibility(View.INVISIBLE);
        this.viewPager.setVisibility(View.VISIBLE);
    }

}
