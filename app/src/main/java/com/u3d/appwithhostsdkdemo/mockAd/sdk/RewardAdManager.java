package com.u3d.appwithhostsdkdemo.mockAd.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RewardAdManager {

    static final String INTENT_AD_IMG_URL = "adImgUrl";
    static final String INTENT_AD_CLICK_URL = "adClickUrl";
    static final String INTENT_AD_INSTANCE_ID = "adInstanceId";
    static final String INTENT_AD_INTENT = "adProxyIntent";

    // These events may from different processes, for example
    // Game Activity runs in game process, and it init a RewardAdManager and create a RewardAdInstance
    // This RewardAdInstance in registered in game process, but when the user call the showAd() API
    // of RewardAdInstance, it will creates a Ad Activity which runs in default process, so the events
    // generated in the Ad Activity will not be transferred to game process
    // That's why we need crossProcessReceiver
    static final String BC_ACTION = "action";
    static final String BC_PARAM_INSTANCE_ID = "instanceId";
    static final String BC_PARAM_TYPE = "type";

    // Broadcast events in multi processes
    static final String BC_EVENT_AD_CLOSE = "adClose";
    static final String BC_PARAM_IS_END = "isEnd";
    static final String BC_EVENT_AD_CLICK = "adClick";

    private static RewardAdManager instance;

    public static synchronized RewardAdManager getInstance() {
        if (instance == null) {
            instance = new RewardAdManager();
        }

        return instance;
    }

    public void init(Context context) {
        IntentFilter filter = new IntentFilter(BC_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(crossProcessReceiver, filter,
                    Context.RECEIVER_EXPORTED);
        } else {
            ContextCompat.registerReceiver(context, crossProcessReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    private final BroadcastReceiver crossProcessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String instanceId = intent.getStringExtra(BC_PARAM_INSTANCE_ID);
            String evt = intent.getStringExtra(BC_PARAM_TYPE);
            RewardAdInstance instance = getRewardAdInstance(instanceId);
            if (evt == null || instance == null || instance.getListener() == null) {
                return;
            }

            switch (evt) {
                case BC_EVENT_AD_CLOSE:
                    boolean isEnd = intent.getBooleanExtra(BC_PARAM_IS_END, false);
                    instance.getListener().onRewardClose(isEnd);
                    break;
                case BC_EVENT_AD_CLICK:
                    instance.getListener().onAdClicked();
                    break;
                default:
                    break;
            }
        }
    };


    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<String, WeakReference<RewardAdInstance>> instancePool = new ConcurrentHashMap<>();

    public RewardAdInstance createRewardAdInstance(Activity activity) {
        RewardAdInstance instance = new RewardAdInstance(activity);
        instancePool.put(instance.getId(), new WeakReference<>(instance));
        return instance;
    }

    void cleanup() {
        Iterator<Map.Entry<String, WeakReference<RewardAdInstance>>> it = instancePool.entrySet().iterator();
        while (it.hasNext()) {
            WeakReference<RewardAdInstance> ref = it.next().getValue();
            if (ref.get() == null) {
                it.remove();
            }
        }
    }

    RewardAdInstance getRewardAdInstance(String instanceId) {
        if (instanceId == null) {
            return null;
        }

        lock.readLock().lock();
        try {
            WeakReference<RewardAdInstance> ref = instancePool.get(instanceId);
            return ref != null ? ref.get() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    static void sendRewardAdEvent(Context context, String instanceId, String evtType, DataInjector injector) {
        Intent broadcast = new Intent(BC_ACTION);
        broadcast.putExtra(BC_PARAM_TYPE, evtType);
        broadcast.putExtra(BC_PARAM_INSTANCE_ID, instanceId);
        if (injector != null) {
            injector.injectData(broadcast);
        }

        broadcast.setPackage(context.getPackageName());
        broadcast.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        context.sendBroadcast(broadcast);
    }

    interface DataInjector {
        void injectData(Intent intent);
    }
}
