package com.u3d.appwithhostsdkdemo.login;

import com.u3d.appwithhostsdkdemo.util.SharedPreferencesManager;
import com.u3d.webglhost.toolkit.multiproc.MultiProcessLauncher;

// This class is used to mock a login SDK, which provides APIs:
// getCode(): Return Code of your auth system, which is used to get Session Info of a user by code2Session API
// Reference: https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
//
// getUserId(): Identify a User, can be used in setGameStartOptions in Game Activity
// User Id is used to separate the users' data in Host Runtime SDK
// Reference of File System in Host SDK: https://developers.weixin.qq.com/minigame/dev/guide/base-ability/file-system.html
public class LoginMockSDK {
    private final static String SP_KEY_USER_ID = "setting_user_id";

    private final static String DEFAULT_USER_ID = "000000-000000-000000-000000";

    // We assume that our code is just User ID
    public static String getCode() {
        return getUserId();
    }

    public static String getUserId() {
        return SharedPreferencesManager.getInstance().getString(SP_KEY_USER_ID, DEFAULT_USER_ID);
    }

    public static void changeUser(String userId) {
        SharedPreferencesManager.getInstance().putString(SP_KEY_USER_ID, userId);

        MultiProcessLauncher.terminateAllGame();
    }

}
