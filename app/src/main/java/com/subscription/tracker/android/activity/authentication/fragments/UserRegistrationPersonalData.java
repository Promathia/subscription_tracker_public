package com.subscription.tracker.android.activity.authentication.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.main.VisitorActivity;
import com.subscription.tracker.android.dialogs.DatePickerFragmentDialog;
import com.subscription.tracker.android.entity.request.UserCreatePostRequest;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.RequestService;
import com.subscription.tracker.android.services.SharedPreferencesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.subscription.tracker.android.Constants.API_URI;

public class UserRegistrationPersonalData extends Fragment implements DatePickerDialog.OnDateSetListener {

    private EditText firstName, lastName, birthDate;
    private ImageButton nextButton;
    private SingleUserData singleUserData;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    public UserRegistrationPersonalData(SingleUserData singleUserData) {
        this.singleUserData = singleUserData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_data_personal, container, false);
        this.firstName = view.findViewById(R.id.personal_data_edit_first_name);
        this.lastName = view.findViewById(R.id.personal_data_edit_last_name);
        this.birthDate = view.findViewById(R.id.personal_data_edit_birth_date);
        this.birthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragmentDialog(UserRegistrationPersonalData.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        initializeNextButton(view);
        return view;
    }

    private void initializeNextButton(View view) {
        nextButton = view.findViewById(R.id.personal_info_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasErrors = false;
                String firstNameText = firstName.getText().toString().trim();
                if (firstNameText.isEmpty()) {
                    firstName.setError(getString(R.string.personal_data_field_required));
                    firstName.requestFocus();
                    hasErrors = true;
                }
                String lastNameText = lastName.getText().toString().trim();
                if (lastNameText.isEmpty()) {
                    lastName.setError(getString(R.string.personal_data_field_required));
                    lastName.requestFocus();
                    hasErrors = true;
                }
                String birthDateText = birthDate.getText().toString().trim();
                if (birthDateText.isEmpty()) {
                    birthDate.setError(getString(R.string.personal_data_field_required));
                    hasErrors = true;
                }
                if (!hasErrors) {
                    nextButton.setEnabled(false);
                    executeProfileUpdate(firstNameText, lastNameText, birthDateText);
                }
            }
        });
    }

    private void executeProfileUpdate(String firstNameText, String lastNameText, String birthDateText) {
        singleUserData.setFirstName(firstNameText);
        singleUserData.setLastName(lastNameText);
        try {
            singleUserData.setBirthDate(simpleDateFormat.parse(birthDateText));
            JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(getPostRequestBody(singleUserData)));
            RequestService.getInstance(getContext()).addToRequestQueue(preparePostUserRequest(jsonObject));
        } catch (ParseException | JsonProcessingException | JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.error_general), Toast.LENGTH_SHORT).show();
        }
    }

    private UserCreatePostRequest getPostRequestBody(final SingleUserData singleUserData) {
        final UserCreatePostRequest userRequest = new UserCreatePostRequest();
        userRequest.setExternalId(singleUserData.getExternalId());
        userRequest.setFirstName(singleUserData.getFirstName());
        userRequest.setLastName(singleUserData.getLastName());
        userRequest.setPhone(singleUserData.getPhone());
        userRequest.setEmail(singleUserData.getEmail());
        userRequest.setBirthDate(singleUserData.getBirthDate());
        userRequest.setRegistrationType(singleUserData.getRegistrationType());
        userRequest.setClubId(singleUserData.getUserClubs().get(0).getClub().getId());
        return userRequest;
    }

    private JsonRequest<JSONObject> preparePostUserRequest(final JSONObject jsonObject) {
        final String url = String.format("%s/users", API_URI);
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleUserCreatedSuccessfully(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        nextButton.setEnabled(true);
                        Toast.makeText(getContext(), getString(R.string.error_user_creation), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleUserCreatedSuccessfully(JSONObject response) {
        try {
            singleUserData.setId(response.getInt("id"));
            final Intent intent = new Intent(getActivity(), VisitorActivity.class);
            new SharedPreferencesService(getContext()).storeObject(R.string.preference_user_data_key, singleUserData);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (JSONException e) {
            Toast.makeText(getContext(), getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        birthDate.setText(simpleDateFormat.format(calendar.getTime()));
        birthDate.setError(null);
    }

}
