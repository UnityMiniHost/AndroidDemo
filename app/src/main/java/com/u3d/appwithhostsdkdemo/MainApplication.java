package com.u3d.appwithhostsdkdemo;

import android.app.Application;
import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.appwithhostsdkdemo.mockAd.sdk.RewardAdManager;
import com.u3d.appwithhostsdkdemo.util.SharedPreferencesManager;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initComponents();
        initMockAdSDK();
    }

    private void initComponents() {
        PropertiesManager.init(this);
        SharedPreferencesManager.init(this);
    }

    private void initMockAdSDK() {
        RewardAdManager.getInstance().init(this);
    }
}
