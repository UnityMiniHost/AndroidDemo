package com.u3d.appwithhostsdkdemo.recentlyPlayed;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.u3d.appwithhostsdkdemo.IHistoryServiceInterface;
import com.u3d.webglhost.toolkit.data.history.HistoryModel;

public class HistoryService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    private final IHistoryServiceInterface.Stub mStub = new IHistoryServiceInterface.Stub() {
        @Override
        public void requestSaveHistory(String historyModelStr) {
            Gson gson = new Gson();
            HistoryModel history = gson.fromJson(historyModelStr, HistoryModel.class);
            HistoryManager.savePlayedGames(history);
        }
    };
}
