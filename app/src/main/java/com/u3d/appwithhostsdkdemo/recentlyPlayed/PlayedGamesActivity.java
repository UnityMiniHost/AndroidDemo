package com.u3d.appwithhostsdkdemo.recentlyPlayed;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.u3d.appwithhostsdkdemo.MainActivity;
import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.home.model.GameModel;
import com.u3d.appwithhostsdkdemo.home.ui.VerticalGameListAdapter;
import com.u3d.appwithhostsdkdemo.home.util.GamePreferencesUtil;
import com.u3d.webglhost.toolkit.multiproc.MultiProcessLauncher;

public class PlayedGamesActivity extends AppCompatActivity {

    VerticalGameListAdapter verticalGameListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUIOnResume();
    }

    private void initUI() {
        initVerticalGameList();
    }

    private void initVerticalGameList() {
        setContentView(R.layout.activity_played_games);

        RecyclerView allGamesRv = findViewById(R.id.allGamesRv);
        allGamesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        verticalGameListAdapter = new VerticalGameListAdapter(this, GamePreferencesUtil.readPlayedGames(this));
        verticalGameListAdapter.setOnItemClickListener(this::startGameAndUpdatePlayedHistory);
        allGamesRv.setAdapter(verticalGameListAdapter);

        View backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());
    }

    private void updateUIOnResume() {
        updateVerticalGameList();
    }

    private void updateVerticalGameList() {
        verticalGameListAdapter.updateGamesModels(GamePreferencesUtil.readPlayedGames(this));
    }

    private void startGameAndUpdatePlayedHistory(GameModel model) {
        if (!MainActivity.isTJHostHandleInitialized) {
            Log.e("PlayedGamesActivity", "TJHostHandle is not initialized!");
            Toast.makeText(this, "TJHostHandle is not initialized!", Toast.LENGTH_SHORT).show();
            return;
        }

        GamePreferencesUtil.savePlayedGames(this, model);

        Log.i("MultiProcess", "gameId: " + model.getId() + " title: " + model.getName());
        MultiProcessLauncher.launch(this, model.getId(), intent -> {
            intent.putExtra("v8LibPath", MainActivity.v8LibPath);
            intent.putExtra("isTempSession", false);
            intent.putExtra("title", model.getName());
        });
    }
}
