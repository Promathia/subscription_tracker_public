package com.subscription.tracker.android.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.tracker.android.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferencesService {

    private final Context context;

    public SharedPreferencesService(final Context context) {
        this.context = context;
    }

    public <T> void storeObject(final int key, final T object) {
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(object));
            final SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(context.getString(key), jsonObject.toString());
            editor.apply();
        } catch (JSONException | JsonProcessingException e) {
            //TODO
        }
    }

    public <T> T getObject(final int key, final Class<T> type) {
        final SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String result = sharedPref.getString(context.getString(key), "");
        if (result.isEmpty()) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(result, type);
        } catch (JsonProcessingException e) {
            //TODO
        }
        return null;
    }

}
