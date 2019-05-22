package com.example.hpur.spr.UI.Utils;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hpur.spr.Logic.Queries.TokBoxServerSDKCallback;
import org.json.JSONException;
import org.json.JSONObject;


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
        String httpUrl = "https://us-central1-sprfinalproject.cloudfunctions.net/opentokSessionId";

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, httpUrl,
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