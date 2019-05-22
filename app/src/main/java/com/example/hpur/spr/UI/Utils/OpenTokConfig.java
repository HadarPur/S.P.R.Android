package com.example.hpur.spr.UI.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hpur.spr.Logic.Queries.TokBoxServerSDKCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import static android.content.Context.WIFI_SERVICE;
import static org.webrtc.ContextUtils.getApplicationContext;

public class OpenTokConfig {

    private RequestQueue requestQueue;
    private Context mCtx;
    private String mApiKey;
    private String mSessionId;
    private String mTokenPublisher;
    private String mTokenSubscriber;

    public OpenTokConfig(Context ctx) {
        this.mCtx = ctx;
        requestQueue = Volley.newRequestQueue(ctx);
    }

    public void tokboxHttpJsonRequest(final TokBoxServerSDKCallback callback) {
        String ip = "172.40.1.47";
        String JsonDevice = "http://"+ip+":5000";

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, JsonDevice,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            mApiKey = response.getString("apiKey");
                            mSessionId = response.getString("sessionId");
                            mTokenPublisher = response.getString("tokenPublisher");
                            mTokenSubscriber = response.getString("tokenSubscriber");

                            callback.onTokboxRequestSucceed(mApiKey, mSessionId, mTokenPublisher, mTokenSubscriber);

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onTokboxRequestFailed();
                        Log.e("Volley", "Error: " + error);

                    }
                }
        );

        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);
    }

}