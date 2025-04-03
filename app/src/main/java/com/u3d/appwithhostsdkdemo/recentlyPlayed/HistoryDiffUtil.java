package com.u3d.appwithhostsdkdemo.recentlyPlayed;

import androidx.recyclerview.widget.DiffUtil;

import com.u3d.webglhost.toolkit.data.history.HistoryModel;

import java.util.List;
import java.util.Objects;

public class HistoryDiffUtil extends DiffUtil.Callback {

    private final List<HistoryModel> oldList;
    private final List<HistoryModel> newList;

    public HistoryDiffUtil(List<HistoryModel> oldList, List<HistoryModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        HistoryModel oldModel = oldList.get(oldItemPosition);
        HistoryModel newModel = newList.get(newItemPosition);

        return Objects.equals(oldModel.getName(), newModel.getName()) &&
                oldModel.getTags() != null && newModel.getTags() != null && oldModel.getTags().equals(newModel.getTags()) &&
                Objects.equals(oldModel.getBriefIntro(), newModel.getBriefIntro()) &&
                Objects.equals(oldModel.getIconUrl(), newModel.getIconUrl()) &&
                Objects.equals(oldModel.getLaunchKey(), newModel.getLaunchKey());
    }
}
