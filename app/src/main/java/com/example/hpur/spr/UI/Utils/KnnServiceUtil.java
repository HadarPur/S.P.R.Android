package com.example.hpur.spr.UI.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.example.hpur.spr.Logic.AddressLocation;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.KnnCallback;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class KnnServiceUtil {

    private final String mHttpUrl = "https://us-central1-sprfinalproject.cloudfunctions.net/knnService";
    private final String mAgentUidKey = "agent_uid";
    private Context mCtx;
    private String mAgentUid;

    public KnnServiceUtil(Context ctx){
        this.mCtx = ctx;
    }

    public void knnServiceJsonRequest(final KnnCallback callback, UserModel userModel, Activity activity) throws JSONException {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();

        String[] separated = userModel.getBirthday().split("/");
        int age = UtilitiesFunc.convertDateToAge(Integer.parseInt(separated[2]),Integer.parseInt(separated[1]),Integer.parseInt(separated[0]));
        AddressLocation addressLocation = UtilitiesFunc.getLocation(userModel.getLiving(), activity);

        jsonBody.put("age",age);
        jsonBody.put("gender",userModel.getGenderType().name());
        jsonBody.put("sector",userModel.getSectorType().name());
        jsonBody.put("sex",userModel.getSexType().name());
        jsonBody.put("latitude", addressLocation.getLatitude());
        jsonBody.put("longitude", addressLocation.getLongitude());

        Log.d("KnnServiceUtil", "latitude = "+addressLocation.getLatitude());
        Log.d("KnnServiceUtil", "longitude = "+addressLocation.getLongitude());

        RequestBody body = RequestBody.create(MEDIA_TYPE, jsonBody.toString());

        Request request = new Request.Builder()
                .url(mHttpUrl)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("failure Response", e.getMessage());
                callback.onKnnServiceRequestFailed();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        mAgentUid = jsonObject.getString(mAgentUidKey);
                        Log.d("OkHttp isSuccessful", "agent: " + mAgentUid);
                        callback.onKnnServiceRequestOnSuccess(mAgentUid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String errorBodyString = response.body().string();
                    Log.d("OkHttp !isSuccessful", "error: " + errorBodyString);
                    callback.onKnnServiceRequestFailed();
                }
            }
        });
    }
}
