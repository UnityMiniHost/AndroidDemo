package com.u3d.appwithhostsdkdemo.mockAd;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.u3d.appwithhostsdkdemo.mockAd.sdk.RewardAdError;
import com.u3d.appwithhostsdkdemo.mockAd.sdk.RewardAdInstance;
import com.u3d.appwithhostsdkdemo.mockAd.sdk.RewardAdListener;
import com.u3d.appwithhostsdkdemo.mockAd.sdk.RewardAdManager;
import com.u3d.webglhost.toolkit.TJHostHandle;

import java.util.Locale;

public class MockAdManager {
    private final Activity activity;

    public MockAdManager(Activity activity) {
        this.activity = activity;
    }

    private RewardAdInstance rewardVideoAd;

    private boolean isLoadInStart = false;

    public void initRewardAd(TJHostHandle hostHandle) {
        if (rewardVideoAd != null) {
            return;
        }

        rewardVideoAd = RewardAdManager.getInstance().createRewardAdInstance(activity);
        rewardVideoAd.setListener(new RewardAdListener() {
            @Override
            public void onAdLoadSuccess() {
                Toast.makeText(activity, "onAdLoadSuccess", Toast.LENGTH_SHORT).show();
                String script = "rewardedVideoLoadCallbackFromJava();";
                if (hostHandle == null) {
                    return;
                }
                hostHandle.runCustomScript(script, bundle -> Log.i("AdManager", "onVideoAdLoadSuccess rewardedVideoLoadCallbackFromJava end"));

                if (isLoadInStart) {
                    showAdIfNeeded();
                    isLoadInStart = false;
                }
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(activity, "onAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardClose(boolean isEnded) {
                Log.i("MockAdManager", "onRewardClose " + isEnded);
                Toast.makeText(activity, "onVideoAdClosed " + isEnded, Toast.LENGTH_SHORT).show();
                String script = String.format("rewardedVideoCloseCallbackFromJava(%s);", isEnded);
                if (hostHandle == null) {
                    return;
                }
                hostHandle.runCustomScript(script, bundle -> Log.i("AdManager", "onVideoAdClosed rewardedVideoLoadCallbackFromJava end"));
            }

            @Override
            public void onAdError(RewardAdError error) {
                Toast.makeText(activity, "onVideoAdPlayError", Toast.LENGTH_SHORT).show();
                String script = String.format(Locale.CHINA, "rewardedVideoErrorCallbackFromJava('%s', %d);", error.getMessage(), error.getCode());
                if (hostHandle == null) {
                    return;
                }
                hostHandle.runCustomScript(script, bundle -> Log.i("AdManager", "onVideoAdPlayError rewardedVideoErrorCallbackFromJava end"));
            }
        });
    }

    public void loadRewardAd() {
        if (rewardVideoAd != null) {
            rewardVideoAd.loadAd();
        }
    }

    public void playRewardAd() {
        if (rewardVideoAd == null) {
            return;
        }

        if (!rewardVideoAd.isReady()) {
            Log.i("MockAdManager", "isLoadInStart = true;");
            isLoadInStart = true;
            rewardVideoAd.loadAd();
        }

        showAdIfNeeded();
    }

    private void showAdIfNeeded() {
        if (rewardVideoAd != null && rewardVideoAd.isReady()) {
            rewardVideoAd.showAd();
        } else {
            Log.e("MockAdManager", "showAdIfNeeded failed");
        }
    }
}
