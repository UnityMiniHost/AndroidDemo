package com.u3d.appwithhostsdkdemo.home.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.home.model.GameModel;

import java.util.List;

public class VerticalGameListAdapter extends GameListAdapter {
    private final Context context;

    public VerticalGameListAdapter(Context context, List<GameModel> models) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_vertical_game_list_item, parent, false);
        return new VerticalGameListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VerticalGameListItemViewHolder itemHolder = (VerticalGameListItemViewHolder) holder;

        GameModel gameInfo = gameModels.get(position);
        Glide.with(context)
                .load(gameInfo.getIconUrl())
                .into(itemHolder.icon);
        itemHolder.name.setText(gameInfo.getName());
        itemHolder.tags.removeAllViews();

        List<String> tags = gameInfo.getTags();
        for (int i = 0; i < tags.size(); i ++) {
            RoundedTagView tagView = new RoundedTagView(context, tags.get(i));
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tagView.getLayoutParams();
            if (i == 0) {
                params.leftMargin = 0;
            }
            tagView.setLayoutParams(params);
            itemHolder.tags.addView(tagView);
        }
        itemHolder.desc.setText(gameInfo.getBriefIntro());
        itemHolder.playBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(gameInfo);
            }
        });

        // Set Ripple Effect
        itemHolder.view.setOnClickListener(v -> {});
    }

    static class VerticalGameListItemViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView icon;
        TextView name;
        LinearLayout tags;
        TextView desc;
        View playBtn;

        public VerticalGameListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            icon = itemView.findViewById(R.id.iconIv);
            name = itemView.findViewById(R.id.nameTv);
            tags = itemView.findViewById(R.id.tagsLl);
            desc = itemView.findViewById(R.id.descTv);
            playBtn = itemView.findViewById(R.id.playRl);
        }
    }
}
