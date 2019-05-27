package com.example.hpur.spr.UI.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.KnnCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

    private int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);

        return ageInt;
    }


    public void knnServiceJsonRequest(final KnnCallback callback, UserModel userModel) throws JSONException {

        JSONObject jsonBody = new JSONObject();

        String[] separated = userModel.getBirthday().split("/");
        int age = getAge(Integer.parseInt(separated[2]),Integer.parseInt(separated[1]),Integer.parseInt(separated[0]));

        jsonBody.put("age",age);
        jsonBody.put("gender",userModel.getGenderType().name());
        jsonBody.put("sector",userModel.getSectorType().name());
        jsonBody.put("sex",userModel.getSexType().name());
        jsonBody.put("latitude","31.771959");
        jsonBody.put("longitude","35.217018");

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
