package com.u3d.appwithhostsdkdemo.login;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.u3d.appwithhostsdkdemo.ILoginCallback;
import com.u3d.appwithhostsdkdemo.ILoginServiceInterface;

// This Service is used receive login / getUserId / getCode requests from Game Activity(Game Process)
// Code is used to get Session Info of a user
// References:
// https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
// https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
public class LoginService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    private final ILoginServiceInterface.Stub mStub = new ILoginServiceInterface.Stub() {
        @Override
        public void requesetLogin(final ILoginCallback callback) throws RemoteException {
            callback.loginEnd(LoginMockSDK.getCode());
        }
    };
}
