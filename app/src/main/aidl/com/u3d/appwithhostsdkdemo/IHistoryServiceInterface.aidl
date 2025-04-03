// IHistoryServiceInterface.aidl
package com.u3d.appwithhostsdkdemo;

// Declare any non-default types here with import statements

interface IHistoryServiceInterface {
    oneway void requestSaveHistory(String historyModelStr);
}