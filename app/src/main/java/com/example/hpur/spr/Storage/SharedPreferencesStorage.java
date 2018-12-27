package com.example.hpur.spr.Storage;

import android.content.Context;

public class SharedPreferencesStorage {
    private final String FILE = "SPR";
    private final Context mContext;

    // c'tor
    public SharedPreferencesStorage(Context context) {
        this.mContext = context;
    }

    // saveData last user's email into shared preferences
    public void saveData(String val, String sharedPreferencesKey) {
        android.content.SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(sharedPreferencesKey, val).apply();
    }

    // read last user's email from shared preferences
    private String readData(String defaultValue, String sharedPreferencesKey) {
        android.content.SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        String readVal = sharedPreferences.getString(sharedPreferencesKey, defaultValue);
        return readVal;

    }

    // read last user's email from shared preferences
    public String readData(String sharedPreferencesKey) {
        return readData("", sharedPreferencesKey);
    }
}
