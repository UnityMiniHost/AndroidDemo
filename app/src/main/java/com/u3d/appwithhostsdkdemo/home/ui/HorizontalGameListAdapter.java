package com.u3d.appwithhostsdkdemo.home.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.home.model.GameModel;

import java.util.List;

public class HorizontalGameListAdapter extends GameListAdapter {

    private final Context context;

    public HorizontalGameListAdapter(Context context, List<GameModel> models) {
        this.context = context;
        this.gameModels = models;
    }

    @Override
    public int getItemCount() {
        return gameModels.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_horizontal_game_list_item, parent, false);
        return new HorizontalGameListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HorizontalGameListItemViewHolder itemHolder = (HorizontalGameListItemViewHolder) holder;

        GameModel gameInfo = gameModels.get(position);
        Glide.with(context)
                .load(gameInfo.getIconUrl())
                .into(itemHolder.icon);
        itemHolder.name.setText(gameInfo.getName());

        itemHolder.view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(gameInfo);
            }
        });
    }

    static class HorizontalGameListItemViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView icon;
        TextView name;

        public HorizontalGameListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            icon = itemView.findViewById(R.id.iconIv);
            name = itemView.findViewById(R.id.nameTv);
        }
    }
}
