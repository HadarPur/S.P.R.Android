package com.example.hpur.spr.UI.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.TokBoxServerSDKCallback;
import com.example.hpur.spr.UI.VideoActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


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

    public void sendCallNotification(FirebaseFirestore firebaseFirestore, final Context ctx, String name, String message, String uid, String agentUid, String apiKey, String sessionId, String tokenPublisher, String tokenSubscriber) {
        HashMap<String, Object> notificationMessage = new HashMap();
        notificationMessage.put("name", name);
        notificationMessage.put("message", message);
        notificationMessage.put("from", uid);
        notificationMessage.put("to", agentUid);
        notificationMessage.put("apiKey", apiKey);
        notificationMessage.put("sessionId", sessionId);
        notificationMessage.put("tokenPublisher", tokenPublisher);
        notificationMessage.put("tokenSubscriber", tokenSubscriber);

        firebaseFirestore.collection("Users").document(uid).collection("Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Notification", "Notification sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Notification", "Error: "+e.getMessage());
            }
        });
    }

}