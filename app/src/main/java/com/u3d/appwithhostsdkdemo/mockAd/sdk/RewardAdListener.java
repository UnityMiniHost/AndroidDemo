package com.u3d.appwithhostsdkdemo.mockAd.sdk;

public interface RewardAdListener {

    void onAdLoadSuccess();
    void onAdClicked();
    void onRewardClose(boolean isEnded);
    void onAdError(RewardAdError error);
}
