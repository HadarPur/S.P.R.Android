package com.example.hpur.spr.Storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.hpur.spr.Logic.Models.ShelterModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferencesStorage {
    private final String FILE = "SPR";
    private final String SHELTERS_FILE = "Shelters";

    private Context mContext;

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
