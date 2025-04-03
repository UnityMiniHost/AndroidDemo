package com.u3d.appwithhostsdkdemo.home.ui;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.u3d.appwithhostsdkdemo.recentlyPlayed.HistoryDiffUtil;
import com.u3d.webglhost.toolkit.data.history.HistoryModel;

import java.util.List;

public abstract class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<HistoryModel> historyModels;

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    protected OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onClick(HistoryModel model);
    }

    public void setOnItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    public void updateHistory(List<HistoryModel> models) {
        DiffUtil.Callback diffCallback = new HistoryDiffUtil(historyModels, models);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.historyModels.clear();
        this.historyModels.addAll(models);

        diffResult.dispatchUpdatesTo(this);
    }
}
