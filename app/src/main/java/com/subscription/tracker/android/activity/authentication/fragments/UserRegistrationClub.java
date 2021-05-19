package com.subscription.tracker.android.activity.authentication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.authentication.AfterSignInActivity;
import com.subscription.tracker.android.adapters.auth.SimpleSpinnerAdapter;
import com.subscription.tracker.android.adapters.auth.ClubListAdapter;
import com.subscription.tracker.android.entity.response.Club;
import com.subscription.tracker.android.entity.response.Role;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserClub;
import com.subscription.tracker.android.services.RequestService;
import com.subscription.tracker.android.services.utils.RoleUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.subscription.tracker.android.Constants.API_URI;

public class UserRegistrationClub extends Fragment {

    private List<Club> clubs = new ArrayList<>();
    private ListView listView;
    private TextView textPleaseSelectClub;
    private TextView textNoClubsFound;
    private ClubListAdapter clubListAdapter;
    private SingleUserData singleUserData;
    private AfterSignInActivity afterSignInActivity;

    public UserRegistrationClub(SingleUserData singleUserData, AfterSignInActivity afterSignInActivity) {
        this.singleUserData = singleUserData;
        this.afterSignInActivity = afterSignInActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_data_club, container, false);
        initializeSpinner(view);
        initializeListView(view);
        initializeNextButton(view);
        return view;
    }

    private void initializeNextButton(View view) {
        ImageButton next = view.findViewById(R.id.club_selection_next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!singleUserData.getUserClubs().isEmpty()) {
                    ViewPager2 viewPager = afterSignInActivity.getViewPager();
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.club_selection_no_club_selected), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeListView(View activityView) {
        this.clubListAdapter = new ClubListAdapter(getActivity(), clubs);
        this.listView = activityView.findViewById(R.id.club_selection_list_view);
        this.textPleaseSelectClub = activityView.findViewById(R.id.club_selection_text_select_club);
        this.textNoClubsFound = activityView.findViewById(R.id.club_selection_text_no_clubs);
        listView.setAdapter(clubListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemView, int position, long l) {
                singleUserData.setUserClubs(prepareClubData(clubs.get(position)));
            }
        });
    }

    private List<UserClub> prepareClubData(final Club club) {
        final List<UserClub> userClubs = new ArrayList<>();
        final UserClub userClub = new UserClub();
        userClub.setClub(club);
        userClub.setActiveClub(true);
        userClub.setUserAccepted(false);
        userClub.setRole(new Role(RoleUtils.Roles.VISITOR.getRoleId(),
                        RoleUtils.Roles.VISITOR.name()));
        userClubs.add(userClub);
        return userClubs;
    }

    private void initializeSpinner(View view) {
        Spinner spinnerCity = view.findViewById(R.id.spinner_city_select);
        final String[] cities = getActivity().getResources().getStringArray(R.array.cities);
        ArrayAdapter arrayAdapter =
                new SimpleSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, cities);
        spinnerCity.setAdapter(arrayAdapter);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != 0) {
                    List<UserClub> userClubs = singleUserData.getUserClubs();
                    if (userClubs != null && !userClubs.isEmpty()) {
                        singleUserData.getUserClubs().clear(); //reset club selection
                    }
                    RequestService.getInstance(getActivity()).addToRequestQueue(getRequest(cities[position]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private JsonArrayRequest getRequest(String city) {
        String url = String.format("%s/city/%s/clubs", API_URI, city);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        handleResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.error_getting_club_data_by_api), Toast.LENGTH_LONG).show();
            }
        });
        return jsonObjectRequest;
    }

    private void handleResponse(JSONArray response) {
        try {
            this.clubs = new ObjectMapper().readValue(response.toString(), new TypeReference<List<Club>>() {
            });
            if (clubs == null || clubs.isEmpty()) {
                textNoClubsFound.setVisibility(View.VISIBLE);
                textPleaseSelectClub.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            } else {
                textNoClubsFound.setVisibility(View.GONE);
                textPleaseSelectClub.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
            }
            clubListAdapter.setClubs(clubs);
            clubListAdapter.notifyDataSetChanged();
        } catch (JsonProcessingException e) {
            Toast.makeText(getActivity(), getString(R.string.error_getting_club_data_by_api), Toast.LENGTH_LONG).show();
        }
    }

}
