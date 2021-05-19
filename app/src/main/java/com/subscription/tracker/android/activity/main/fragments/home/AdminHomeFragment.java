package com.subscription.tracker.android.activity.main.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.adapters.main.UserListAdapter;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserClub;
import com.subscription.tracker.android.entity.response.UserForClubResponse;
import com.subscription.tracker.android.services.RequestService;
import com.subscription.tracker.android.services.SharedPreferencesService;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.subscription.tracker.android.Constants.API_URI;

public class AdminHomeFragment extends Fragment {

    private List<UserForClubResponse> users = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UserListAdapter userListAdapter;
    private SingleUserData singleUserResponse;
    private TextView noUsersInList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_admin_owner_inst, container, false);
        recyclerView = root.findViewById(R.id.admin_user_list);
        recyclerView.setHasFixedSize(true);
        noUsersInList = root.findViewById(R.id.admin_no_users_in_list);
        return root;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.linearLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(linearLayoutManager);
        String clubId = getClubId();
        if (clubId != null && !clubId.isEmpty()) {
            RequestService.getInstance(getActivity())
                    .addToRequestQueue(getUsersListRequest(clubId));
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_getting_users_for_club_api), Toast.LENGTH_LONG).show();
        }
    }

    private String getClubId() {
        this.singleUserResponse = new SharedPreferencesService(getContext())
                        .getObject(R.string.preference_user_data_key, SingleUserData.class);;
        if (this.singleUserResponse == null) {
            return "";
        }
        for (final UserClub userClub : singleUserResponse.getUserClubs()) {
            if (userClub.isActiveClub()) {
                return String.valueOf(userClub.getClub().getId());
            }
        }
        return "";
    }

    private JsonArrayRequest getUsersListRequest(String clubId) {
        final String url = String.format("%s/club/%s/users", API_URI, clubId);
        return new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        handleUsersListResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.error_getting_users_list_by_api), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleUsersListResponse(JSONArray response) {
        try {
            users = new ObjectMapper().readValue(response.toString(), new TypeReference<List<UserForClubResponse>>() {});
            filterCurrentUserFromUsers();
            if (users != null && !users.isEmpty()) {
                userListAdapter = new UserListAdapter(users, this);
                recyclerView.setAdapter(this.userListAdapter);
            } else {
                recyclerView.setVisibility(View.GONE);
                noUsersInList.setVisibility(View.VISIBLE);
            }
        } catch (JsonProcessingException e) {
            Toast.makeText(getActivity(), getString(R.string.error_getting_users_list_by_api), Toast.LENGTH_LONG).show();
        }
    }

    private void filterCurrentUserFromUsers() {
        if (singleUserResponse == null) {
            return;
        }
        int currentUserId = singleUserResponse.getId();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == currentUserId) {
                users.remove(i);
            }
        }
    }

}