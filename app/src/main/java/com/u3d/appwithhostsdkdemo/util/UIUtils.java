package com.u3d.appwithhostsdkdemo.util;

import android.content.Context;

public class UIUtils {

    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
