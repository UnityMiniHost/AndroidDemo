package com.u3d.appwithhostsdkdemo.ad;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.webglhost.toolkit.TJHostHandle;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.reward.WMRewardAd;
import com.windmill.sdk.reward.WMRewardAdListener;
import com.windmill.sdk.reward.WMRewardAdRequest;
import com.windmill.sdk.reward.WMRewardInfo;

import java.util.HashMap;

public class AdManager {
    public AdManager(Context context) {
        this.mContext = context;
    }

    private final Context mContext;
    private WMRewardAd rewardVideoAd;
    private boolean rewardVideoAdShouldSendReward = false;

    private boolean isLoadInStart = false;

    public void initAds(TJHostHandle hostHandle) {
        setUpSharedAdsConfig();
        initRewardAd(hostHandle);
    }

    public void setUpSharedAdsConfig() {
        WindMillAd ads = WindMillAd.sharedAds();
        ads.setAdult(true);
        ads.setPersonalizedAdvertisingOn(true);
        ads.setDebugEnable(false);
        ads.startWithAppId(mContext, PropertiesManager.getInstance().getProperty("tobid.app.id"));
        WindMillAd.requestPermission((Activity) mContext);
    }

    public void initRewardAd(TJHostHandle hostHandle) {
        if (rewardVideoAd != null) {
            return;
        }

        PropertiesManager p = PropertiesManager.getInstance();
        WMRewardAdRequest request = new WMRewardAdRequest(p.getProperty("tobid.placement.id"), p.getProperty("tobid.user.id"), null);

        rewardVideoAd = new WMRewardAd((Activity) mContext, request);
        rewardVideoAd.setRewardedAdListener(new WMRewardAdListener() {
            @Override
            public void onVideoAdLoadSuccess(String placementId) {
                Toast.makeText(mContext, "onVideoAdLoadSuccess", Toast.LENGTH_SHORT).show();
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
            public void onVideoAdPlayStart(AdInfo adInfo) {
                Toast.makeText(mContext, "onVideoAdPlayStart", Toast.LENGTH_SHORT).show();
                rewardVideoAdShouldSendReward = false;
            }

            @Override
            public void onVideoAdPlayEnd(AdInfo adInfo) {
                Toast.makeText(mContext, "onVideoAdPlayEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoAdClicked(AdInfo adInfo) {
                Toast.makeText(mContext, "onVideoAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoRewarded(AdInfo adInfo, WMRewardInfo rewardInfo) {
                Toast.makeText(mContext, "onVideoRewarded", Toast.LENGTH_SHORT).show();
                rewardVideoAdShouldSendReward = true;
            }

            @Override
            public void onVideoAdClosed(AdInfo adInfo) {
                Toast.makeText(mContext, "onVideoAdClosed " + (rewardVideoAdShouldSendReward? "true": "false"), Toast.LENGTH_SHORT).show();
                String script = String.format("rewardedVideoCloseCallbackFromJava(%s);", rewardVideoAdShouldSendReward);
                if (hostHandle == null) {
                    return;
                }
                hostHandle.runCustomScript(script, bundle -> Log.i("AdManager", "onVideoAdClosed rewardedVideoLoadCallbackFromJava end"));
            }

            @Override
            public void onVideoAdLoadError(final WindMillError error, final String placementId) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                String script = String.format("rewardedVideoErrorCallbackFromJava('%s', %d);", error.getMessage(), error.getErrorCode());
                if (hostHandle == null) {
                    return;
                }
                hostHandle.runCustomScript(script, bundle -> Log.i("AdManager", "onVideoAdLoadError rewardedVideoErrorCallbackFromJava end"));
            }

            @Override
            public void onVideoAdPlayError(final WindMillError error, final String placementId) {
                Toast.makeText(mContext, "onVideoAdPlayError", Toast.LENGTH_SHORT).show();
                String script = String.format("rewardedVideoErrorCallbackFromJava('%s', %d);", error.getMessage(), error.getErrorCode());
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
            isLoadInStart = true;
            rewardVideoAd.loadAd();
        }

        showAdIfNeeded();
    }

    private void showAdIfNeeded() {
        if (rewardVideoAd != null && rewardVideoAd.isReady()) {
            HashMap<String, String> option = new HashMap<>();
            option.put(WMConstants.AD_SCENE_ID, "Demo_App_Id");
            option.put(WMConstants.AD_SCENE_DESC, "Demo_App_Scene");
            rewardVideoAd.show((Activity) mContext, option);
        } else {
            Log.e("AdManager", "showAdIfPossible failed");
        }
    }
}
