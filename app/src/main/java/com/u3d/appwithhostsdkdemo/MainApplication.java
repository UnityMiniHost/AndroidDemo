package com.u3d.appwithhostsdkdemo;

import android.app.Application;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.UriConfig;
import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.appwithhostsdkdemo.mockAd.sdk.RewardAdManager;
import com.u3d.appwithhostsdkdemo.util.SharedPreferencesManager;

import org.json.JSONObject;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initComponents();
//        initDataFinderSDK(); // Initialize if needed
        initMockAdSDK();
    }

    private void initComponents() {
        PropertiesManager.init(this);
        SharedPreferencesManager.init(this);
    }

    private void initDataFinderSDK() {
        // these codes are used to init DataFinder SDK to upload events and perf data
        PropertiesManager p = PropertiesManager.getInstance();
        final InitConfig config = new InitConfig(p.getProperty("datafinder.id"), p.getProperty("datafinder.channel"));
        config.setUriConfig(UriConfig.createByDomain(p.getProperty("datafinder.domain"), null));
        config.setAutoStart(false);
        config.setAutoTrackEnabled(true);
        config.setLogEnable(false);
        AppLog.setEncryptAndCompress(true);
        AppLog.init(this, config);
        AppLog.start();

        // upload a app_on_create event for test
        JSONObject params = new JSONObject();
        AppLog.onEventV3("app_on_create", params);
        Log.d("HostApplication", "onEventV3 app_on_create event");
    }

    private void initMockAdSDK() {
        RewardAdManager.getInstance().init(this);
    }
}
