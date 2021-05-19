package com.subscription.tracker.android.services;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.request.AddSubscriptionToUserRequest;
import com.subscription.tracker.android.entity.response.Subscription;
import com.subscription.tracker.android.entity.response.UserSubscription;
import com.subscription.tracker.android.listener.FailRequestListener;
import com.subscription.tracker.android.listener.SuccessRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.subscription.tracker.android.Constants.API_URI;

public class SubscriptionService {

    private final Context context;

    public SubscriptionService(final Context context) {
        this.context = context;
    }

    public void populateSubscriptionForUser(final int userId,
                                            final SuccessRequestListener afterRequestUpdateListener,
                                            final FailRequestListener failRequestListener) {
        RequestService.getInstance(context)
                .addToRequestQueue(prepareGetSubscriptionForUserRequest(userId, afterRequestUpdateListener, failRequestListener));
    }

    public void initializeSubscriptions(final List<Subscription> subscriptionList, final int clubId) {
        RequestService.getInstance(context)
                .addToRequestQueue(prepareGetSubscriptionsRequest(subscriptionList, clubId));
    }

    public void addSubscriptionToUser(final int userId,
                                      final Subscription item,
                                      final int initiatorId) {
        try {
            final AddSubscriptionToUserRequest addSubscriptionToUserRequest = new AddSubscriptionToUserRequest();
            addSubscriptionToUserRequest.setInitiatorId(initiatorId);
            addSubscriptionToUserRequest.setTerm(item.getTermDays());
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(addSubscriptionToUserRequest));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareAddSubscriptionToUser(userId, item, jsonObject));
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
    }

    public void useVisitForUser(final UserSubscription userSubscription, final int initiatorId) {
        try {
            final JSONObject body = new JSONObject();
            body.put("initiatorId", String.valueOf(initiatorId));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareUseVisitForUser(userSubscription.getId(), userSubscription.getUserId(), body));
        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
    }

    private JsonObjectRequest prepareUseVisitForUser(final Integer subscriptionId,
                                                     final Integer userId,
                                                     final JSONObject jsonObject) {
        final String url = String.format("%s/user/%s/subscription/%s/use", API_URI, userId, subscriptionId);
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, context.getString(R.string.subscription_use_for_user_successfully), Toast.LENGTH_LONG).show();
                        ((AppCompatActivity) context).finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            Toast.makeText(context, context.getString(R.string.error_missing_access_rights), Toast.LENGTH_LONG).show();
                        } else if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            Toast.makeText(context, context.getString(R.string.error_user_subscription_is_invalid_or_full_used), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private JsonObjectRequest prepareGetSubscriptionForUserRequest(final int userId,
                                                                   final SuccessRequestListener afterRequestUpdateListener,
                                                                   final FailRequestListener failRequestListener) {
        final String url = String.format("%s/user/%s/subscription", API_URI, userId);
        return new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserSubscription userSubscription = new ObjectMapper().readValue(response.toString(), UserSubscription.class);
                            afterRequestUpdateListener.doUpdate(userSubscription);
                        } catch (JsonProcessingException e) {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            failRequestListener.doUpdate();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private JsonObjectRequest prepareAddSubscriptionToUser(final int userId,
                                                           final Subscription item,
                                                           final JSONObject jsonObject) {
        final String url = String.format("%s/user/%s/subscription/%s/add", API_URI, userId, item.getId());
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, context.getString(R.string.subscription_add_to_user_successfully), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            Toast.makeText(context, context.getString(R.string.error_missing_access_rights), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private JsonArrayRequest prepareGetSubscriptionsRequest(final List<Subscription> subscriptionList, int clubId) {
        final String url = String.format("%s/club/%s/subscriptions", API_URI, clubId);
        return new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            subscriptionList.addAll(new ObjectMapper().readValue(response.toString(), new TypeReference<List<Subscription>>() {
                            }));
                        } catch (JsonProcessingException e) {
                            Toast.makeText(context, context.getString(R.string.error_while_getting_subscriptions), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, context.getString(R.string.error_while_getting_subscriptions), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
