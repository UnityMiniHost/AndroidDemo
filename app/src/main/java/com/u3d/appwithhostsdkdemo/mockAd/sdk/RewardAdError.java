package com.u3d.appwithhostsdkdemo.mockAd.sdk;

import java.util.Locale;

public enum RewardAdError {

    // Error in fetch Ad source in Connect Server
    FAILED_TO_GET_AD_SRC(1001, "Failed to get AD src"),

    // Error in load img in Glide
    FAILED_TO_LOAD_AD_MAT(2001, "Failed to get AD material"),

    // Error in show ad in Activity
    FAILED_TO_SHOW_AD(3001, "Failed to show AD");

    private final int code;
    private final String message;

    RewardAdError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return String.format(Locale.CHINA, "Error Code: %d, Error Message: %s", code, message);
    }
}
