package com.subscription.tracker.android.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.Constants;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.adapters.auth.SimpleSpinnerAdapter;
import com.subscription.tracker.android.entity.request.UserUpdatePostRequest;
import com.subscription.tracker.android.entity.response.UserForClubResponse;
import com.subscription.tracker.android.listener.SuccessRequestListener;
import com.subscription.tracker.android.services.RequestService;
import com.subscription.tracker.android.services.utils.RoleUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.subscription.tracker.android.Constants.API_URI;

public class VisitorInfoDialog extends DialogFragment {

    private EditText userName;
    private EditText userSurname;
    private EditText userEmail;
    private EditText userPhone;
    private EditText userBirthdate;
    private Spinner spinner;
    private TableLayout tableWithUserData;
    private Button saveButton;
    private ProgressBar progressBar;
    private int userId;
    private int userClubId;
    private int requestorId;
    private int requestorRoleId;
    private UserForClubResponse userForClubResponseInitial;
    private final List<String> roles = new ArrayList<>();
    private final Map<String, Integer> roleToId = new HashMap<>();
    private final UserUpdatePostRequest userUpdatePayloadData = new UserUpdatePostRequest();
    private VisitorInfoDialog currentDialog;
    private final SuccessRequestListener<UserUpdatePostRequest> successListener;

    public VisitorInfoDialog(final SuccessRequestListener<UserUpdatePostRequest> successListener) {
        this.successListener = successListener;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_visitor_info, container, false);
        this.userName = view.findViewById(R.id.edit_user_name);
        this.userSurname = view.findViewById(R.id.edit_user_surname);
        this.userEmail = view.findViewById(R.id.edit_user_email);
        this.userPhone = view.findViewById(R.id.edit_user_phone);
        this.userBirthdate = view.findViewById(R.id.edit_user_birthdate);
        this.tableWithUserData = view.findViewById(R.id.table_with_user_data);
        this.saveButton = view.findViewById(R.id.edit_user_data_save);
        this.progressBar = view.findViewById(R.id.user_data_progress_bar);
        this.userId = getArguments().getInt("userId");
        this.userClubId = getArguments().getInt("userClubId");
        this.requestorId = getArguments().getInt("requestorId");
        this.requestorRoleId = getArguments().getInt("requestorRoleId");
        this.currentDialog = this;
        RequestService.getInstance(getActivity()).addToRequestQueue(getUsersDataRequest(view));
        return view;
    }

    private void initializeSaveButton() {
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userNameInitial = userForClubResponseInitial.getFirstName();
                final String userSurnameInitial = userForClubResponseInitial.getLastName();
                final String userEmailInitial = userForClubResponseInitial.getEmail();
                final String userPhoneInitial = userForClubResponseInitial.getPhone();
                final String userBirthdateInitial = Constants.DATE_FORMATTER.format(userForClubResponseInitial.getBirthDate());
                final int userRoleInitial = userForClubResponseInitial.getRole().getId();
                final String userNameCurrent = userName.getText().toString();
                final String userSurnameCurrent = userSurname.getText().toString();
                final String userEmailCurrent = userEmail.getText().toString();
                final String userPhoneCurrent = userPhone.getText().toString();
                final String userBirthdateCurrent = userBirthdate.getText().toString();
                int rolePositionInSpinner = spinner.getSelectedItemPosition();
                if (!userNameInitial.equals(userNameCurrent)) {
                    userUpdatePayloadData.setUserName(userNameCurrent);
                }
                if (!userSurnameInitial.equals(userSurnameCurrent)) {
                    userUpdatePayloadData.setUserSurname(userSurnameCurrent);
                }
                if (userEmailInitial != null && !userEmailInitial.equals(userEmailCurrent)) {
                    userUpdatePayloadData.setUserEmail(userEmailCurrent);
                }
                if (userPhoneInitial != null && !userPhoneInitial.equals(userPhoneCurrent)) {
                    userUpdatePayloadData.setUserPhone(userPhoneCurrent);
                }
                if (!userBirthdateInitial.equals(userBirthdateCurrent)) {
                    userUpdatePayloadData.setUserBirthdate(userBirthdateCurrent);
                }
                Integer userRoleCurrent = roleToId.get(roles.get(rolePositionInSpinner));
                if (userRoleCurrent > 0 && userRoleInitial != userRoleCurrent) {//0 - label
                    userUpdatePayloadData.setUserRole(String.valueOf(userRoleCurrent));
                }
                if (userUpdatePayloadData.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.user_edit_dialog_nothing_changed_message), Toast.LENGTH_LONG).show();
                } else {
                    userUpdatePayloadData.setInitiatorId(String.valueOf(requestorId));
                    RequestService.getInstance(getActivity()).addToRequestQueue(getUserUpdateRequest());
                }
            }
        });
    }

    private JsonObjectRequest getUserUpdateRequest() {
        final String url = String.format("%s/club/%s/users/%s", API_URI, userClubId, userForClubResponseInitial.getId());
        return new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                prepareJsonData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity(), getString(R.string.user_edit_dialog_update_success), Toast.LENGTH_LONG).show();
                        successListener.doUpdate(userUpdatePayloadData);
                        currentDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.error_updating_user_data_by_api), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private JSONObject prepareJsonData() {
        try {
            return new JSONObject(new ObjectMapper().writeValueAsString(userUpdatePayloadData));
        } catch (JsonProcessingException | JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
        return new JSONObject();
    }

    private JsonObjectRequest getUsersDataRequest(final View view) {
        final String url = String.format("%s/club/%s/user/%s", API_URI, userClubId, userId);
        return new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleUsersDataResponse(response, view);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.error_getting_user_data_by_api), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleUsersDataResponse(final JSONObject response, final View view) {
        try {
            this.userForClubResponseInitial = new ObjectMapper().readValue(response.toString(), UserForClubResponse.class);
            this.userName.setText(userForClubResponseInitial.getFirstName());
            this.userSurname.setText(userForClubResponseInitial.getLastName());
            final String email = userForClubResponseInitial.getEmail() != null ? userForClubResponseInitial.getEmail() : "-";
            this.userEmail.setText(email);
            final String phone = userForClubResponseInitial.getPhone() != null ? userForClubResponseInitial.getPhone() : "-";
            this.userPhone.setText(phone);
            this.userBirthdate.setText(Constants.DATE_FORMATTER.format(userForClubResponseInitial.getBirthDate()));
            initializeRolesSpinner(view, userForClubResponseInitial);
            setFieldsAccessibleBasedOnRole();
            initializeSaveButton();
            makeFieldsVisible();
        } catch (final JsonProcessingException e) {
            Toast.makeText(getActivity(), getString(R.string.error_getting_user_data_by_api), Toast.LENGTH_LONG).show();
        }

    }

    private void setFieldsAccessibleBasedOnRole() {
        if (userForClubResponseInitial.getRole().getId() <= requestorRoleId || requestorRoleId == 3) {
            this.userName.setEnabled(false);
            this.userSurname.setEnabled(false);
            this.userEmail.setEnabled(false);
            this.userPhone.setEnabled(false);
            this.userBirthdate.setEnabled(false);
            this.spinner.setEnabled(false);
        }
    }

    private void makeFieldsVisible() {
        this.progressBar.setVisibility(View.GONE);
        this.tableWithUserData.setVisibility(View.VISIBLE);
        this.saveButton.setVisibility(View.VISIBLE);
    }

    private void initializeRolesSpinner(final View view, final UserForClubResponse userForClubResponse) {
        this.spinner = view.findViewById(R.id.edit_user_role);
        prepareRolesForDisplay();
        final SimpleSpinnerAdapter arrayAdapter =
                new SimpleSpinnerAdapter(getActivity(), R.layout.simple_spinner_dropdown_item, roles.toArray(new String[0]));
        spinner.setAdapter(arrayAdapter);
        String userRoleLabel = "";
        for (final Map.Entry<String, Integer> entry : roleToId.entrySet()) {
            if (userForClubResponse.getRole().getId() == entry.getValue()) {
                userRoleLabel = entry.getKey();
            }
        }
        spinner.setSelection(roles.indexOf(userRoleLabel));
    }

    /**
     * Since android spinner receives array only we initialize
     * collection and -> into array, then we initialize a map as well
     * so that when an item is selected from spinner we can find it in
     * the map.
     */
    private void prepareRolesForDisplay() {
        this.roles.add(this.getString(R.string.user_role_spinner_label)); //dummy element for spinner label
        this.roleToId.put(this.getString(R.string.user_role_spinner_label), -1); //dummy element for spinner label
        for (final RoleUtils.Roles value : RoleUtils.Roles.values()) {
            if (RoleUtils.isRoleAllowedToModify(requestorRoleId, value)) {
                final String roleLabel = this.getString(value.getRoleTextId());
                this.roles.add(roleLabel);
                this.roleToId.put(roleLabel, value.getRoleId());
            }
        }
    }

}
