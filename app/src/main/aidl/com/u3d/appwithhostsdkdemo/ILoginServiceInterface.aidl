// ILoginServiceInterface.aidl
package com.u3d.appwithhostsdkdemo;

import com.u3d.appwithhostsdkdemo.ILoginCallback;

// Declare any non-default types here with import statements

interface ILoginServiceInterface {
    void requesetLogin(ILoginCallback callback);
}