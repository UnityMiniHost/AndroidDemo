package com.u3d.appwithhostsdkdemo.config;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
    private static final String TAG = "PropertiesManager";
    private static final String CONFIG_FILE_PATH = "properties/config.properties";

    private static volatile PropertiesManager instance;
    private final Context appContext;
    private Properties properties;

    private PropertiesManager(Context context) {
        this.appContext = context.getApplicationContext();
        loadProperties();
    }

    public static void init(Context context) {
        instance = new PropertiesManager(context);
    }

    public static PropertiesManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("PropertiesManager is not initialized");
        }
        return instance;
    }

    private void loadProperties() {
        try {
            properties = new Properties();
            InputStream inputStream = appContext.getAssets().open(CONFIG_FILE_PATH);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Read config file failed", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}