package com.u3d.appwithhostsdkdemo.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.u3d.appwithhostsdkdemo.MainActivity;
import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.appwithhostsdkdemo.home.http.GameService;
import com.u3d.appwithhostsdkdemo.home.ui.HorizontalHistoryAdapter;
import com.u3d.appwithhostsdkdemo.home.ui.HorizontalHistoryItemDecoration;
import com.u3d.appwithhostsdkdemo.home.ui.SettingModal;
import com.u3d.appwithhostsdkdemo.home.ui.VerticalGameAdapter;
import com.u3d.appwithhostsdkdemo.recentlyPlayed.HistoryManager;
import com.u3d.appwithhostsdkdemo.recentlyPlayed.PlayedGamesActivity;
import com.u3d.appwithhostsdkdemo.util.UIUtils;
import com.u3d.webglhost.toolkit.data.history.HistoryModel;
import com.u3d.webglhost.toolkit.model.GameModel;
import com.u3d.webglhost.toolkit.multiproc.MultiProcessLauncher;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View mRootView;
    private View playedGamesSection;
    private HorizontalHistoryAdapter playedGameListAdapter;
    private VerticalGameAdapter verticalGameListAdapter;
    private List<GameModel> allGames;
    private final Handler handler = new Handler(Looper.getMainLooper()); // Get main thread handler to Update UI
    private boolean isLoadingAllGames = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_home, container, false);
            initUI(mRootView);
        } else {
            if (mRootView.getParent() != null) {
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
        }

        return mRootView;
    }

    private void initUI(View view) {
        initPlayedGames(view);
        initAllGames(view);
        initSetting(view);
    }

    private void initPlayedGames(View view) {
        playedGamesSection = view.findViewById(R.id.playedGames);

        View moreView = view.findViewById(R.id.moreTv);
        moreView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PlayedGamesActivity.class);
            startActivity(intent, null);
        });

        RecyclerView playedGamesRv = view.findViewById(R.id.playedGamesRv);
        playedGamesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        playedGamesRv.addItemDecoration(new HorizontalHistoryItemDecoration(UIUtils.dpToPx(getContext(), 24)));

        playedGameListAdapter = new HorizontalHistoryAdapter(getContext(), new ArrayList<>());
        playedGameListAdapter.setOnItemClickListener((history) -> startGame(history.getLaunchKey(), history.getName()));
        playedGamesRv.setAdapter(playedGameListAdapter);

        updatePlayedHistory();
    }

    private void updatePlayedHistory() {
        handler.post(() -> {
            if (getContext() == null) {
                return;
            }
            List<HistoryModel> history = HistoryManager.readPlayedHistory();
            playedGameListAdapter.updateHistory(history);
            playedGamesSection.setVisibility(history.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    // Try to get game list from Host Server or Demo App's server
    private void initAllGames(View view) {
        View title = view.findViewById(R.id.title);
        title.setOnClickListener(v -> updateAllGames());

        RecyclerView allGamesRv = view.findViewById(R.id.allGamesRv);
        allGamesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        allGames = new ArrayList<>();
        verticalGameListAdapter = new VerticalGameAdapter(getContext(), allGames);
        verticalGameListAdapter.setOnItemClickListener((game) -> startGame(game.getLaunchKey(), game.getName()));

        allGamesRv.setAdapter(verticalGameListAdapter);

        updateAllGames();
    }

    private void updateAllGames() {
        Toast.makeText(getContext(), "Updating Game List", Toast.LENGTH_SHORT).show();

        if (isLoadingAllGames) {
            return;
        }

        isLoadingAllGames = true;
        PropertiesManager p = PropertiesManager.getInstance();
        GameService.getHostServerGameList(p.getProperty("app.service.token"), new GameService.GetHostServerGameListListener() {
            @Override
            public void onSuccess(List<GameModel> list) {
                allGames = list;
                handler.post(() -> verticalGameListAdapter.updateHistory(list));
                isLoadingAllGames = false;
            }

            @Override
            public void onFailure(Throwable throwable) {
                handler.post(() -> Toast.makeText(getContext(), "Get game list failed", Toast.LENGTH_LONG).show());
                isLoadingAllGames = false;
            }
        });
    }

    private void startGame(String launchKey, String title) {
        Context context = getContext();
        if (context == null) {
            Log.e("HomeFragment", "Failed to getContext() in startGameAndUpdatePlayedHistory()");
            return;
        }

        if (!MainActivity.isTJHostHandleInitialized) {
            Log.e("HomeFragment", "TJHostHandle is not initialized!");
            Toast.makeText(context, "TJHostHandle is not initialized!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (launchKey == null || launchKey.isEmpty()) {
            Log.e("HomeFragment", "launchKey is null or empty!");
            return;
        }

        Log.i("MultiProcess", "launchKey: " + launchKey + " title: " + title);
        MultiProcessLauncher.launch(context, launchKey, intent -> {
            intent.putExtra("v8LibPath", MainActivity.v8LibPath);
            intent.putExtra("isTempSession", false);
            intent.putExtra("title", title);
        });
    }

    private void initSetting(View view) {
        ImageView settingIv = view.findViewById(R.id.settingIv);
        settingIv.setOnClickListener(v -> {
            SettingModal dialogFragment = new SettingModal();
            dialogFragment.show(getChildFragmentManager(), "customDialog");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlayedHistory();
    }
}
