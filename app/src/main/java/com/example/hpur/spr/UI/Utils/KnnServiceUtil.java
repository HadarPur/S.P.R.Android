package com.example.hpur.spr.UI.Utils;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hpur.spr.Logic.AddressLocation;
import com.example.hpur.spr.Logic.Models.AgentModel;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.KnnCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

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

    public void knnServiceJsonRequest(final KnnCallback callback, UserModel userModel, ArrayList<AgentModel> agents, Activity activity) throws JSONException {
        JSONArray jsonArray = getAgentsJsonArray(activity , agents);
        JSONObject jsonBody = getJsonBody(activity, userModel, jsonArray);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, mHttpUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            mAgentUid = response.getString(mAgentUidKey);
                            Log.d("knnServiceJsonRequest", "agent_uid = " + mAgentUid);
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
        mRequestQueue.add(jsonObjectRequest);
    }

    private JSONObject getJsonBody(Activity activity, UserModel userModel, JSONArray jsonArray) throws JSONException {
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

        jsonBody.put("onlineAgents", jsonArray);

        return jsonBody;
    }

    private JSONArray getAgentsJsonArray(Activity activity , ArrayList<AgentModel> agents) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (AgentModel agentModel:agents) {
            JSONObject jsonObject = new JSONObject();

            String[] separated = agentModel.getBirthday().split("/");
            int age = UtilitiesFunc.convertDateToAge(Integer.parseInt(separated[2]),Integer.parseInt(separated[1]),Integer.parseInt(separated[0]));
            AddressLocation addressLocation = UtilitiesFunc.getLocation(agentModel.getLiving(), activity);


            jsonObject.put("age",age);
            jsonObject.put("gender",agentModel.getGenderType().name());
            jsonObject.put("sector",agentModel.getSectorType().name());
            jsonObject.put("sex",agentModel.getSexType().name());
            jsonObject.put("latitude",String.valueOf(addressLocation.getLatitude()));
            jsonObject.put("longitude",String.valueOf(addressLocation.getLongitude()));
            jsonObject.put("uid",agentModel.getAgentUID());

            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }


    public void sendCallNotification(FirebaseFirestore firebaseFirestore, final Context ctx, String name, String message, String uid, String agentUid, String type, String activityName) {
        HashMap<String, Object> notificationMessage = new HashMap();
        notificationMessage.put("activityType", type);
        notificationMessage.put("activityNameAction", activityName);
        notificationMessage.put("name", name);
        notificationMessage.put("message", message);
        notificationMessage.put("from", uid);
        notificationMessage.put("to", agentUid);

        firebaseFirestore.collection("Users").document(uid).collection("Notifications-Chats").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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