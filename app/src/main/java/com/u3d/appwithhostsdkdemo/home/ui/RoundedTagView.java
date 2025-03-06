package com.u3d.appwithhostsdkdemo.home.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.util.UIUtils;

public class RoundedTagView extends RoundedLayout {

    TextView textView;

    protected static final float DEFAULT_CORNER_RADIUS = 8;
    protected static final float DEFAULT_START_END_PADDING = 8;

    public RoundedTagView(Context context) {
        super(context);
        init(context, null, null);
    }

    public RoundedTagView(Context context, String tag) {
        super(context);
        init(context, null, tag);
    }

    public RoundedTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null);
    }

    public RoundedTagView(Context context, AttributeSet attrs, String tag) {
        super(context, attrs);
        init(context, attrs, tag);
    }

    private void init(Context context, AttributeSet attrs, String tag) {
        textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.text_light_grey, null));
        textView.setTextSize(10);
        textView.setText(tag);
        textView.setMaxLines(1);
        addView(textView);

        if (cornerRadius == RoundedLayout.DEFAULT_CORNER_RADIUS) {
            cornerRadius = UIUtils.dpToPx(context, RoundedTagView.DEFAULT_CORNER_RADIUS);
        }

        invalidateOutline();

        int startEndPadding = UIUtils.dpToPx(context, RoundedTagView.DEFAULT_START_END_PADDING);
        setPadding(startEndPadding, 0, startEndPadding, 0);

        setBackgroundColor(getResources().getColor(R.color.tag_bg, null));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)getLayoutParams();
        if (params == null) {
            params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        params.leftMargin = UIUtils.dpToPx(context, 4);
        setLayoutParams(params);
    }
}
