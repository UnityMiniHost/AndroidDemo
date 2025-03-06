package com.u3d.appwithhostsdkdemo.home.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.util.UIUtils;

public class RoundedLayout extends FrameLayout {

    protected float cornerRadius;

    protected static final float DEFAULT_CORNER_RADIUS = 0;

    public RoundedLayout(Context context) {
        super(context);
        init(context,null);
    }

    public RoundedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try (TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedLayout)) {
            cornerRadius = typedArray.getDimension(R.styleable.RoundedLayout_cornerRadius, DEFAULT_CORNER_RADIUS);
        }

        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
    }

    public void setCornerRadius(float radius) {
        this.cornerRadius = UIUtils.dpToPx(getContext(), radius);
        invalidateOutline();
    }
}
