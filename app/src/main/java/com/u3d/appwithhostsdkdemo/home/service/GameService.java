package com.u3d.appwithhostsdkdemo.home.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.appwithhostsdkdemo.home.model.GameModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class GameService {

    private static final OkHttpClient client = new OkHttpClient();

    private static final String HOST_SERVER_API_BASE = PropertiesManager.getInstance().getProperty("app.host.server.domain");

    interface HttpBaseListener<T> {
        void onSuccess(T var1);

        void onFailure(Throwable var1);
    }

    // Get Game List by JWT Token
    private static final String GET_GAME_LIST_URL = HOST_SERVER_API_BASE + "/game/get_list";

    public interface GetHostServerGameListListener extends HttpBaseListener<List<GameModel>> {
    }

    public static void getHostServerGameList(String jwtToken, GetHostServerGameListListener listener) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("token", jwtToken);

        Request request = new Request.Builder()
                .url(GET_GAME_LIST_URL)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        // Get response body
                        String responseBody = "";
                        if (response.body() != null) {
                            responseBody = response.body().string();
                        }

                        // Convert to List<GameModel>
                        Gson gson = new Gson();
                        GameModel[] gamesArray = gson.fromJson(responseBody, GameModel[].class);
                        List<GameModel> gameList = new ArrayList<>();
                        if (gamesArray != null) {
                            Collections.addAll(gameList, gamesArray);
                        }
                        listener.onSuccess(gameList);
                    } else {
                        Log.e("HttpUtil", "Get Game List failed: " + response.code());
                        listener.onFailure(new Exception("Get Game List failed: " + response.code()));
                    }
                } catch (Exception e) {
                    Log.e("HttpUtil", "Get Game List failed: " + e.getMessage());
                    listener.onFailure(e);
                }
            }
        });
    }

}

