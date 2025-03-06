package com.u3d.appwithhostsdkdemo.home.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.u3d.appwithhostsdkdemo.home.model.GameModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamePreferencesUtil {
    private static ArrayList<GameModel> playedGames;
    private static final String PREFS_NAME = "played_games_prefs";
    private static final String KEY_PLAYED_GAMES = "played_games";
    public static List<GameModel> readPlayedGames(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String playedGamesStr = sp.getString(KEY_PLAYED_GAMES, null);

        playedGames = new ArrayList<>();
        if (playedGamesStr != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<GameModel>>() {}.getType();
            playedGames = gson.fromJson(playedGamesStr, type);
        }

        return playedGames;
    }

    public static void savePlayedGames(Context context, GameModel model) {
        boolean isExistsInPlayedGames = false;
        for (int i = 0; i < playedGames.size(); i ++) {
            if (Objects.equals(playedGames.get(i).getId(), model.getId())) {
                playedGames.set(i, playedGames.get(0));
                playedGames.set(0, model);
                isExistsInPlayedGames = true;
                break;
            }
        }

        if (!isExistsInPlayedGames) {
            playedGames.add(0, model);
        }

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String updatedPlayedGamesStr = gson.toJson(playedGames);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_PLAYED_GAMES, updatedPlayedGamesStr);
        editor.apply();
    }
}
