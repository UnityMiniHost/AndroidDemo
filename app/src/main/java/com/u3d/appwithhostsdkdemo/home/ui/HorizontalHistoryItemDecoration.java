package com.u3d.appwithhostsdkdemo.home.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalHistoryItemDecoration extends RecyclerView.ItemDecoration {

    private final int horizontalSpaceWidth;

    public HorizontalHistoryItemDecoration(int horizontalSpaceWidth) {
        this.horizontalSpaceWidth = horizontalSpaceWidth;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        if (position != 0) {
            outRect.left = horizontalSpaceWidth;
        } else {
            outRect.left = 0;
        }
    }
}
