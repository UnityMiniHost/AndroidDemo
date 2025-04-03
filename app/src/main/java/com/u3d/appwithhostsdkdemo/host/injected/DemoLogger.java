package com.u3d.appwithhostsdkdemo.host.injected;

import android.util.Log;

import com.u3d.webglhost.log.LogPrinter;

public class DemoLogger implements LogPrinter {
    @Override
    public void println(int level, String tag, String msg) {
        // You can just use Log to print it, or upload to ur server, or just ignore, as u like
        Log.println(level, tag, msg);
    }
}
