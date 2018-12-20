package com.example.hpur.spr.Storage;

import android.content.Context;

public class SharedPreferencesStorage {
    private final String API_KEY = "api_key";
    private final Context mContext;

    // c'tor
    public SharedPreferencesStorage(Context context) {
        mContext = context;
    }

    // saveData last user's email/mode into shared preferences
    public void saveData(String val, String sharedPreferencesKey) {
        android.content.SharedPreferences sharedPreferences = mContext.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(sharedPreferencesKey, val).apply();
    }

    // read last user's email/mode from shared preferences
    public String readData(String sharedPreferencesKey, String defaultValue) {
        android.content.SharedPreferences sharedPreferences = mContext.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
        String readVal=sharedPreferences.getString(sharedPreferencesKey, defaultValue);
        return readVal;

    }
    public String readData(String sharedPreferencesKey) {
        return readData(sharedPreferencesKey, "");
    }
}
