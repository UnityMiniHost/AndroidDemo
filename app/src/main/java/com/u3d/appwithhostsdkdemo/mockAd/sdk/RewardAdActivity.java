package com.u3d.appwithhostsdkdemo.mockAd.sdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.u3d.appwithhostsdkdemo.R;

import java.util.Locale;

public class RewardAdActivity extends AppCompatActivity {

    private TextView timeTv;

    private boolean isAdEnd;

    private String instanceId;

    private long timeLeftInMillis = COUNT_DOWN_DURATION;
    private static final int COUNT_DOWN_DURATION = 10 * 1000;

    private CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        hideTopBarIfNeeded();
    }

    private void initViews() {
        // Set full screen style
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().setAttributes(lp);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Bind views
        setContentView(R.layout.activity_reward_ad);

        ImageView adIv = findViewById(R.id.adIv);
        timeTv = findViewById(R.id.timeTv);
        View closeBtn = findViewById(R.id.closeIv);

        Intent intent = getIntent();
        String adImgUrl = intent.getStringExtra(RewardAdManager.INTENT_AD_IMG_URL);

        Glide.with(this)
                .load(adImgUrl)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(adIv);

        adIv.setOnClickListener((v) -> onAdClick());
        closeBtn.setOnClickListener((v) -> finishWithResult());

        instanceId = getIntent().getStringExtra(RewardAdManager.INTENT_AD_INSTANCE_ID);
    }

    private void hideTopBarIfNeeded() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAdEnd) {
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                timeTv.setText(String.format(Locale.CHINA, "还剩 %d 秒获得奖励", millisUntilFinished / 1000 + 1));
            }

            @Override
            public void onFinish() {
                timeTv.setText("恭喜获得奖励");
                isAdEnd = true;
            }
        };
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void finishWithResult() {
        RewardAdManager.sendRewardAdEvent(getApplicationContext(), instanceId, RewardAdManager.BC_EVENT_AD_CLOSE, intent -> {
            intent.putExtra(RewardAdManager.BC_PARAM_IS_END, isAdEnd);
        });
        finish();
    }

    private void onAdClick() {
        RewardAdManager.sendRewardAdEvent(getApplicationContext(), instanceId, RewardAdManager.BC_EVENT_AD_CLICK, null);
        jumpToBrowser();
    }

    private void jumpToBrowser() {
        Intent intent = getIntent();
        String adClickUrl = intent.getStringExtra(RewardAdManager.INTENT_AD_CLICK_URL);
        if (adClickUrl != null && !adClickUrl.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adClickUrl));
            startActivity(browserIntent);
        }
    }
}
