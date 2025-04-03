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
import com.u3d.webglhost.toolkit.data.history.HistoryModel;

import java.util.List;

public class VerticalHistoryAdapter extends HistoryAdapter {
    private final Context context;

    public VerticalHistoryAdapter(Context context, List<HistoryModel> models) {
        this.context = context;
        this.historyModels = models;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_vertical_game_list_item, parent, false);
        return new VerticalHistoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VerticalHistoryItemViewHolder itemHolder = (VerticalHistoryItemViewHolder) holder;

        HistoryModel gameInfo = historyModels.get(position);
        Glide.with(context)
                .load(gameInfo.getIconUrl())
                .into(itemHolder.icon);
        itemHolder.name.setText(gameInfo.getName());
        itemHolder.tags.removeAllViews();

        List<String> tags = gameInfo.getTags();
        for (int i = 0; tags != null && i < tags.size(); i ++) {
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

    static class VerticalHistoryItemViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView icon;
        TextView name;
        LinearLayout tags;
        TextView desc;
        View playBtn;

        public VerticalHistoryItemViewHolder(@NonNull View itemView) {
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
