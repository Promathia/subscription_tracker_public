package com.subscription.tracker.android.services;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.adapters.main.UserViewHolder;
import com.subscription.tracker.android.entity.request.UserAcceptToClubRequest;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserForClubResponse;
import com.subscription.tracker.android.services.utils.ClubUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.subscription.tracker.android.Constants.API_URI;

public class UserService {

    private final Context context;

    public UserService(final Context context) {
        this.context = context;
    }

    public void executeUserAcceptRequest(final SingleUserData singleUserData,
                                         final UserForClubResponse userForClubResponse,
                                         final UserViewHolder viewHolder) {
        final UserAcceptToClubRequest userAcceptToClubRequest = new UserAcceptToClubRequest();
        userAcceptToClubRequest.setInitiatorId(singleUserData.getId());
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(userAcceptToClubRequest));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareUserAcceptRequestAndHandlers(jsonObject, userForClubResponse, ClubUtils.getActiveClub(singleUserData).getId(), viewHolder));
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
    }

    private Request<JSONObject> prepareUserAcceptRequestAndHandlers(final JSONObject jsonObject,
                                                       final UserForClubResponse userForClubResponse,
                                                       int clubId,
                                                       final UserViewHolder viewHolder) {
        final String url = String.format("%s/clubs/%s/user/%s/accept", API_URI, clubId, userForClubResponse.getId());
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleUserUpdatedSuccessfully(viewHolder, userForClubResponse);
                        viewHolder.notifyItemChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewHolder.getAcceptUserButton().setEnabled(true);
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            Toast.makeText(context, context.getString(R.string.error_missing_access_rights), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleUserUpdatedSuccessfully(final UserViewHolder viewHolder, final UserForClubResponse userForClubResponse) {
        viewHolder.getAcceptUserButton().setEnabled(false);
        viewHolder.getAddSubscriptionButton().setEnabled(true);
        viewHolder.getUseSubscriptionButton().setEnabled(true);
        userForClubResponse.setUserAccepted(true);
    }

}
