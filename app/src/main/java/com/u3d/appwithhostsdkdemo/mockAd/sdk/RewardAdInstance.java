package com.u3d.appwithhostsdkdemo.mockAd.sdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RewardAdInstance {

    private final Activity boundActivity;
    private final String instanceId;
    private RewardAdListener privateListener;
    private boolean isLoading = false;

    private String adImgUrl;
    private String adClickUrl;
    private boolean isLoaded = false;

    public RewardAdInstance(Activity activity) {
        this.boundActivity = activity;
        this.instanceId = UUID.randomUUID().toString();
        bindToActivity();
    }

    RewardAdListener getListener() {
        return this.privateListener;
    }

    public void setListener(RewardAdListener listener) {
        this.privateListener = new WeakReference<>(listener).get();
    }

    public void loadAd() {
        if (isLoading) {
            Log.i("MockAdManager", "isLoading");
            return;
        }
        isLoading = true;
        isLoaded = false;

        if (adImgUrl == null || adImgUrl.isEmpty()) {
            getAdSrc();
        } else {
            loadAdMat();
        }
    }

    private void getAdSrc() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://connect.unity.cn/api/connectapp/v11/ads")
                .addHeader("ConnectAppDebug", "true")
                .addHeader("ConnectAppVersion", "2.5.2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                privateListener.onAdError(RewardAdError.FAILED_TO_GET_AD_SRC);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful() || response.body() == null) {
                    privateListener.onAdError(RewardAdError.FAILED_TO_GET_AD_SRC);
                    return;
                }

                String responseData;
                try {
                    responseData = response.body().string();
                } catch (IOException e) {
                    privateListener.onAdError(RewardAdError.FAILED_TO_GET_AD_SRC);
                    return;
                }

                Gson gson = new Gson();
                AdSrcResponse resp = gson.fromJson(responseData, AdSrcResponse.class);
                adImgUrl = resp.image;
                adClickUrl = resp.url;

                loadAdMat();
            }
        });
    }

    private void loadAdMat() {
        Glide.with(boundActivity)
                .load(adImgUrl)
                .diskCacheStrategy(DiskCacheStrategy.DATA) // 缓存原始数据
                .addListener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                @NonNull Target<Drawable> target, boolean isFirstResource) {
                        isLoading = false;
                        privateListener.onAdError(RewardAdError.FAILED_TO_LOAD_AD_MAT);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model,
                                                   Target<Drawable> target, @NonNull DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.i("MockAdManager", "onResourceReady");
                        isLoaded = true;
                        isLoading = false;
                        privateListener.onAdLoadSuccess();
                        return false;
                    }
                })
                .preload();
    }

    private static class AdSrcResponse {
        String image;
        String url;
    }

    public void showAd() {
        if (!isLoaded) {
            Log.e("RewardAdInstance", "Can not show ad before loading");
            return;
        }

        isLoaded = false;
        Intent intent = new Intent(boundActivity, RewardAdActivity.class);
        intent.putExtra(RewardAdManager.INTENT_AD_IMG_URL, adImgUrl);
        intent.putExtra(RewardAdManager.INTENT_AD_CLICK_URL, adClickUrl);
        intent.putExtra(RewardAdManager.INTENT_AD_INSTANCE_ID, getId());
        boundActivity.startActivity(intent);
    }

    public boolean isReady() {
        return this.isLoaded;
    }

    private final LifecycleObserver lifecycleObserver = new LifecycleEventObserver() {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source,
                                   @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (source == boundActivity) {
                    destroy();
                }
            }
        }
    };

    public void bindToActivity() {
        if (boundActivity != null && boundActivity instanceof LifecycleOwner) {
            LifecycleOwner lifecycleOwner = (LifecycleOwner) boundActivity;
            lifecycleOwner.getLifecycle().addObserver(lifecycleObserver);
        }
    }

    private void destroy() {
        if (boundActivity != null && boundActivity instanceof LifecycleOwner) {
            LifecycleOwner lifecycleOwner = (LifecycleOwner) boundActivity;
            lifecycleOwner.getLifecycle().removeObserver(lifecycleObserver);
        }


        RewardAdManager.getInstance().cleanup();
    }

    String getId() {
        return this.instanceId;
    }
}
