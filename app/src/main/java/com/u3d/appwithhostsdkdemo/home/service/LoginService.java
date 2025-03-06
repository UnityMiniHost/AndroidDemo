package com.u3d.appwithhostsdkdemo.home.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.u3d.appwithhostsdkdemo.ILoginCallback;
import com.u3d.appwithhostsdkdemo.ILoginServiceInterface;

public class LoginService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    private final ILoginServiceInterface.Stub mStub = new ILoginServiceInterface.Stub() {
        @Override
        public void requesetLogin(final ILoginCallback callback) throws RemoteException {
            callback.loginEnd("Mock Code");
        }
    };
}
