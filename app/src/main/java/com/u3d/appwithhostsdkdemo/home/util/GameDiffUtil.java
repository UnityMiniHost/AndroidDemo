package com.u3d.appwithhostsdkdemo.home.util;

import androidx.recyclerview.widget.DiffUtil;

import com.u3d.appwithhostsdkdemo.home.model.GameModel;

import java.util.List;
import java.util.Objects;

public class GameDiffUtil extends DiffUtil.Callback {

    private final List<GameModel> oldList;
    private final List<GameModel> newList;

    public GameDiffUtil(List<GameModel> oldList, List<GameModel> newList) {
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
        GameModel oldModel = oldList.get(oldItemPosition);
        GameModel newModel = newList.get(newItemPosition);

        return Objects.equals(oldModel.getName(), newModel.getName()) &&
                oldModel.getTags() != null && newModel.getTags() != null && oldModel.getTags().equals(newModel.getTags()) &&
                Objects.equals(oldModel.getBriefIntro(), newModel.getBriefIntro()) &&
                Objects.equals(oldModel.getIconUrl(), newModel.getIconUrl());
    }
}
