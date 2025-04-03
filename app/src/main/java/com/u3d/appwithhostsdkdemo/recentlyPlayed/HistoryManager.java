package com.u3d.appwithhostsdkdemo.recentlyPlayed;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.u3d.appwithhostsdkdemo.login.LoginMockSDK;
import com.u3d.appwithhostsdkdemo.util.SharedPreferencesManager;
import com.u3d.webglhost.toolkit.data.history.HistoryModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistoryManager {
    private static final String SP_KEY_USER_HISTORY_FORMAT = "user_history_%s";
    public static List<HistoryModel> readPlayedHistory() {
        String playedGamesStr = SharedPreferencesManager.getInstance().getString(getSPKeyUserHistory(), null);

        List<HistoryModel> historyList = new ArrayList<>();
        if (playedGamesStr != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            historyList = gson.fromJson(playedGamesStr, type);
        }

        return historyList;
    }

    public static void savePlayedGames(HistoryModel history) {
        List<HistoryModel> historyList = readPlayedHistory();

        for (int i = 0; i < historyList.size(); i ++) {
            if (Objects.equals(historyList.get(i).getLaunchKey(), history.getLaunchKey())) {
                historyList.remove(i);
                break;
            }
        }

        historyList.add(0, history);

        Gson gson = new Gson();
        String updatedPlayedGamesStr = gson.toJson(historyList);
        SharedPreferencesManager.getInstance().putString(getSPKeyUserHistory(), updatedPlayedGamesStr);
    }

    private static String getSPKeyUserHistory() {
        return String.format(SP_KEY_USER_HISTORY_FORMAT, LoginMockSDK.getUserId());
    }
}
