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
import com.u3d.appwithhostsdkdemo.home.ui.VerticalHistoryAdapter;
import com.u3d.webglhost.toolkit.multiproc.MultiProcessLauncher;

public class PlayedGamesActivity extends AppCompatActivity {

    VerticalHistoryAdapter historyListAdapter;

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

        historyListAdapter = new VerticalHistoryAdapter(this, HistoryManager.readPlayedHistory());
        historyListAdapter.setOnItemClickListener((history) -> startGame(history.getLaunchKey(), history.getName()));
        allGamesRv.setAdapter(historyListAdapter);

        View backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());
    }

    private void updateUIOnResume() {
        updateVerticalGameList();
    }

    private void updateVerticalGameList() {
        historyListAdapter.updateHistory(HistoryManager.readPlayedHistory());
    }

    private void startGame(String launchKey, String title) {
        if (!MainActivity.isTJHostHandleInitialized) {
            Log.e("PlayedGamesActivity", "TJHostHandle is not initialized!");
            Toast.makeText(this, "TJHostHandle is not initialized!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("MultiProcess", "launchKey: " + launchKey + " title: " +title);
        MultiProcessLauncher.launch(this, launchKey, intent -> {
            intent.putExtra("v8LibPath", MainActivity.v8LibPath);
            intent.putExtra("isTempSession", false);
            intent.putExtra("title", title);
        });
    }
}
