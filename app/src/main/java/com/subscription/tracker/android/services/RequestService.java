package com.subscription.tracker.android.services;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class RequestService {

    private static RequestService instance;
    private RequestQueue requestQueue;
    private Context context;

    private RequestService(final Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestService getInstance(final Context context) {
        if (instance == null) {
            instance = new RequestService(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(final Request<T> req) {
        getRequestQueue().add(req);
    }

    private RequestQueue getRequestQueue() {
        if (this.requestQueue == null) {
            final Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
            final Network network = new BasicNetwork(new HurlStack());
            this.requestQueue = new RequestQueue(cache, network);
            this.requestQueue.start();
        }
        return this.requestQueue;
    }
}
