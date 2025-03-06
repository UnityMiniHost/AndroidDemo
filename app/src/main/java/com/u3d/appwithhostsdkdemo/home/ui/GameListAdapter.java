package com.u3d.appwithhostsdkdemo.home.ui;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.u3d.appwithhostsdkdemo.home.model.GameModel;
import com.u3d.appwithhostsdkdemo.home.util.GameDiffUtil;

import java.util.List;

public abstract class GameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<GameModel> gameModels;

    protected OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(GameModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateGamesModels(List<GameModel> models) {
        DiffUtil.Callback diffCallback = new GameDiffUtil(gameModels, models);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.gameModels.clear();
        this.gameModels.addAll(models);

        diffResult.dispatchUpdatesTo(this);
    }
}
