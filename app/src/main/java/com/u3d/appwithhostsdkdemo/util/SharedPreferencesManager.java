package com.u3d.appwithhostsdkdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "UnityMiniHostPref";
    private static SharedPreferencesManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        instance = new SharedPreferencesManager(context);
    }

    public static SharedPreferencesManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("SharedPreferencesManager is not initialized");
        }
        return instance;
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}