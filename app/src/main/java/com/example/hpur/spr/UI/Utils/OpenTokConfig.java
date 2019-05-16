package com.example.hpur.spr.UI.Utils;


import android.content.Context;
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

public class OpenTokConfig {
//    public static final String SESSION_ID = "2_MX40NjMyODgyMn5-MTU1NzgzMTY3NzEzOH5QbnltSXNXYktSTkprT3FMSXRYRWMwL3J-fg";
//    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NjMyODgyMiZzaWc9NWUzMjg0NTY1YTA3Y2QyZTVmMGY1Mjk0N2RmODdlZDFiNGU2ZGRlOTpzZXNzaW9uX2lkPTJfTVg0ME5qTXlPRGd5TW41LU1UVTFOemd6TVRZM056RXpPSDVRYm5sdFNYTlhZa3RTVGtwclQzRk1TWFJZUldNd0wzSi1mZyZjcmVhdGVfdGltZT0xNTU3ODMxOTMyJm5vbmNlPTAuODI1OTI1NTM3NzU2ODI4JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTE1NjA0MjM5MzEmaW5pdGlhbF9sYXlvdXRfY2xhc3NfbGlzdD0=";
//    public static final String API_KEY = "46328822";
//    public static final String SECRET_KEY = "1031919db407a48f966d8a2e55bc09dce0f43d95";

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

        // TODO: need to change the ip address to wifi ip address!!!!
        String JsonDevice = "http://172.40.0.210:5000";

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