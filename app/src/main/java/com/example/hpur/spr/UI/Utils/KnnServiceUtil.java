package com.example.hpur.spr.UI.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hpur.spr.Logic.AddressLocation;
import com.example.hpur.spr.Logic.Map;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.KnnCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class KnnServiceUtil {
    private final String mHttpUrl = "https://us-central1-sprfinalproject.cloudfunctions.net/knnService";
    private final String mAgentUidKey = "agent_uid";
    private Context mCtx;

    private RequestQueue mRequestQueue;
    private String mAgentUid;

    public KnnServiceUtil(Context ctx){
        this.mCtx = ctx;
        this.mRequestQueue = Volley.newRequestQueue(mCtx);
    }

    public void knnServiceJsonRequest(final KnnCallback callback, UserModel userModel, Activity activity) throws JSONException {

        JSONObject jsonBody = new JSONObject();

        String[] separated = userModel.getBirthday().split("/");
        int age = UtilitiesFunc.convertDateToAge(Integer.parseInt(separated[2]),Integer.parseInt(separated[1]),Integer.parseInt(separated[0]));
        AddressLocation addressLocation = UtilitiesFunc.getLocation(userModel.getLiving(), activity);

        jsonBody.put("age",age);
        jsonBody.put("gender",userModel.getGenderType().name());
        jsonBody.put("sector",userModel.getSectorType().name());
        jsonBody.put("sex",userModel.getSexType().name());
        jsonBody.put("latitude",String.valueOf(addressLocation.getLatitude()));
        jsonBody.put("longitude",String.valueOf(addressLocation.getLongitude()));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, mHttpUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            mAgentUid = response.getString(mAgentUidKey);
                            callback.onKnnServiceRequestOnSuccess(mAgentUid);
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onKnnServiceRequestFailed();
                Log.e("Volley", "Error: " + error);
            }
        });
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }




}
