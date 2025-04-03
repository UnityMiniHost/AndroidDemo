package com.u3d.appwithhostsdkdemo.host.injected;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.u3d.appwithhostsdkdemo.IHistoryServiceInterface;
import com.u3d.appwithhostsdkdemo.recentlyPlayed.HistoryService;
import com.u3d.webglhost.toolkit.data.history.HistoryModel;
import com.u3d.webglhost.toolkit.data.history.HistorySaver;

public class DemoHistorySaver implements HistorySaver {
    private IHistoryServiceInterface historyService;
    private final static String TAG = "DemoHistorySaver";
    private final Context context;

    ServiceConnection historyServiceConnection;

    public DemoHistorySaver(Context context) {
        this.context = context;

        initHistoryServiceConnection();
    }

    @Override
    public void save(HistoryModel historyModel) {
        // You can upload the history info to your server here, we just simply save it to SharedPreference for example
        Gson gson = new Gson();
        Log.i(TAG, "Upload history to server: " + gson.toJson(historyModel));

        if (historyService == null) {
            Log.e(TAG, "HistoryService is not initialized");
            return;
        }

        try {
            historyService.requestSaveHistory(gson.toJson(historyModel));
        } catch (RemoteException e) {
            Log.e(TAG, "requestSaveHistory failed: " + e.getMessage());
        }
    }

    private void initHistoryServiceConnection() {
        historyServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "onServiceConnected");
                historyService = IHistoryServiceInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected");
                historyService = null;
            }
        };

        Intent intent = new Intent(context, HistoryService.class);
        intent.setPackage(context.getPackageName());
        context.bindService(intent, historyServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void destroy() {
        if (historyServiceConnection != null) {
            context.unbindService(historyServiceConnection);
        }
    }
}
