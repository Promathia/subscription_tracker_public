package com.subscription.tracker.android.services;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.main.fragments.clubinfo.ClubInfoViewHolder;
import com.subscription.tracker.android.activity.main.fragments.timetable.TimeTableViewHolder;
import com.subscription.tracker.android.entity.request.ClubInformationRequest;
import com.subscription.tracker.android.entity.request.ClubTimetableRequest;
import com.subscription.tracker.android.entity.response.ClubInformationResponse;
import com.subscription.tracker.android.entity.response.ClubTimetableResponse;
import com.subscription.tracker.android.services.utils.HtmlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.subscription.tracker.android.Constants.API_URI;

public class ClubService {

    private Context context;

    public ClubService(Context context) {
        this.context = context;
    }

    public void setClubInfoText(final ClubInfoViewHolder viewHolder, final int clubId, final boolean isVisitor) {
        RequestService.getInstance(context).addToRequestQueue(prepareGetClubInfoRequest(viewHolder, clubId, isVisitor));
    }

    public void setTimeTableText(final TimeTableViewHolder viewHolder, final int clubId, final boolean isVisitor) {
        RequestService.getInstance(context).addToRequestQueue(prepareGetTimetableRequest(viewHolder, clubId, isVisitor));
    }

    public void updateExistingInformation(final ClubInfoViewHolder viewHolder,
                                          final int clubId,
                                          final CharSequence informationId,
                                          final String html) {
        viewHolder.getProgressBar().setVisibility(View.VISIBLE);
        viewHolder.getScrollView().setVisibility(View.GONE);
        final ClubInformationRequest clubInformationRequest = new ClubInformationRequest();
        clubInformationRequest.setInformation(html);
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(clubInformationRequest));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareUpdateInformationRequest(viewHolder, jsonObject, clubId, informationId));
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
            viewHolder.getProgressBar().setVisibility(View.GONE);
            viewHolder.getScrollView().setVisibility(View.VISIBLE);
        }

    }

    public void createNewInformation(final ClubInfoViewHolder viewHolder,
                                   final int clubId,
                                   final String html) {
        viewHolder.getProgressBar().setVisibility(View.VISIBLE);
        viewHolder.getScrollView().setVisibility(View.GONE);
        final ClubInformationRequest clubInformationRequest = new ClubInformationRequest();
        clubInformationRequest.setInformation(html);
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(clubInformationRequest));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareCreateNewInformationRequest(viewHolder, jsonObject, clubId));
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
            viewHolder.getProgressBar().setVisibility(View.GONE);
            viewHolder.getScrollView().setVisibility(View.VISIBLE);
        }
    }

    public void updateExistingTimetable(final TimeTableViewHolder viewHolder,
                                        final int clubId,
                                        final CharSequence timeTableId,
                                        final String html) {
        viewHolder.getProgressBar().setVisibility(View.VISIBLE);
        viewHolder.getScrollView().setVisibility(View.GONE);
        final ClubTimetableRequest clubTimetableRequest = new ClubTimetableRequest();
        clubTimetableRequest.setTimetable(html);
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(clubTimetableRequest));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareUpdateTimetableRequest(viewHolder, jsonObject, clubId, timeTableId));
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
            viewHolder.getProgressBar().setVisibility(View.GONE);
            viewHolder.getScrollView().setVisibility(View.VISIBLE);
        }

    }

    public void createNewTimetable(final TimeTableViewHolder viewHolder,
                                   final int clubId,
                                   final String html) {
        viewHolder.getProgressBar().setVisibility(View.VISIBLE);
        viewHolder.getScrollView().setVisibility(View.GONE);
        final ClubTimetableRequest clubTimetableRequest = new ClubTimetableRequest();
        clubTimetableRequest.setTimetable(html);
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(clubTimetableRequest));
            RequestService.getInstance(context)
                    .addToRequestQueue(prepareCreateNewTimetableRequest(viewHolder, jsonObject, clubId));
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
            viewHolder.getProgressBar().setVisibility(View.GONE);
            viewHolder.getScrollView().setVisibility(View.VISIBLE);
        }
    }

    private JsonObjectRequest prepareUpdateInformationRequest(final ClubInfoViewHolder viewHolder,
                                                              final JSONObject jsonObject,
                                                              final int clubId,
                                                              final CharSequence informationId) {
        final String url = String.format("%s/club/%s/information/%s", API_URI, clubId, informationId);
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Toast.makeText(context, context.getString(R.string.club_info_updated_successfully), Toast.LENGTH_LONG).show();
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, context.getString(R.string.error_creating_club_info_for_this_club), Toast.LENGTH_LONG).show();
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                });
    }

    private JsonObjectRequest prepareCreateNewInformationRequest(final ClubInfoViewHolder viewHolder,
                                                               final JSONObject jsonObject,
                                                               final int clubId) {
        final String url = String.format("%s/club/%s/information", API_URI, clubId);
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            final ClubTimetableResponse timetableResponse =
                                    new ObjectMapper().readValue(response.toString(), ClubTimetableResponse.class);
                            viewHolder.getmClubInfoId().setText(timetableResponse.getId());
                            Toast.makeText(context, context.getString(R.string.club_info_saved_successfully), Toast.LENGTH_LONG).show();
                        } catch (JsonProcessingException e) {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, context.getString(R.string.error_creating_club_info_for_this_club), Toast.LENGTH_LONG).show();
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                });
    }

    private JsonObjectRequest prepareGetClubInfoRequest(final ClubInfoViewHolder viewHolder,
                                                        final int clubId,
                                                        final boolean isVisitor) {
        final String url = String.format("%s/club/%s/information", API_URI, clubId);
        return new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            final ClubInformationResponse informationResponse =
                                    new ObjectMapper().readValue(response.toString(), ClubInformationResponse.class);
                            if (!isVisitor) {
                                viewHolder.getmEditor().setHtml(informationResponse.getInformation());
                                viewHolder.getmClubInfoId().setText(informationResponse.getId());
                            }
                            viewHolder.getmPreview().setText(HtmlUtils.getSpannedFromHtmlWithAPI(informationResponse.getInformation()));
                            viewHolder.getProgressBar().setVisibility(View.GONE);
                            viewHolder.getScrollView().setVisibility(View.VISIBLE);
                        } catch (JsonProcessingException e) {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            Toast.makeText(context, context.getString(R.string.error_this_club_has_empty_club_info), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                });
    }

    private JsonObjectRequest prepareUpdateTimetableRequest(final TimeTableViewHolder viewHolder,
                                                            final JSONObject jsonObject,
                                                            final int clubId,
                                                            final CharSequence timeTableId) {
        final String url = String.format("%s/club/%s/timetable/%s", API_URI, clubId, timeTableId);
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Toast.makeText(context, context.getString(R.string.timetable_updated_successfully), Toast.LENGTH_LONG).show();
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, context.getString(R.string.error_creating_timetable_for_this_club), Toast.LENGTH_LONG).show();
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                });
    }

    private JsonObjectRequest prepareCreateNewTimetableRequest(final TimeTableViewHolder viewHolder,
                                                               final JSONObject jsonObject,
                                                               final int clubId) {
        final String url = String.format("%s/club/%s/timetable", API_URI, clubId);
        return new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            final ClubTimetableResponse timetableResponse =
                                    new ObjectMapper().readValue(response.toString(), ClubTimetableResponse.class);
                            viewHolder.getmTimetableId().setText(timetableResponse.getId());
                            Toast.makeText(context, context.getString(R.string.timetable_saved_successfully), Toast.LENGTH_LONG).show();
                        } catch (JsonProcessingException e) {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, context.getString(R.string.error_creating_timetable_for_this_club), Toast.LENGTH_LONG).show();
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                });
    }


    private JsonObjectRequest prepareGetTimetableRequest(final TimeTableViewHolder viewHolder,
                                                         final int clubId,
                                                         final boolean isVisitor) {
        final String url = String.format("%s/club/%s/timetable", API_URI, clubId);
        return new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            final ClubTimetableResponse timetableResponse =
                                    new ObjectMapper().readValue(response.toString(), ClubTimetableResponse.class);
                            if (!isVisitor) {
                                viewHolder.getmEditor().setHtml(timetableResponse.getTimetable());
                                viewHolder.getmTimetableId().setText(timetableResponse.getId());
                            }
                            viewHolder.getmPreview().setText(HtmlUtils.getSpannedFromHtmlWithAPI(timetableResponse.getTimetable()));
                            viewHolder.getProgressBar().setVisibility(View.GONE);
                            viewHolder.getScrollView().setVisibility(View.VISIBLE);
                        } catch (JsonProcessingException e) {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            Toast.makeText(context, context.getString(R.string.error_this_club_has_empty_timetable), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_LONG).show();
                        }
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.getScrollView().setVisibility(View.VISIBLE);
                    }
                });
    }

}
