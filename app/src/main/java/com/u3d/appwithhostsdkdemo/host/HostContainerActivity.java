package com.u3d.appwithhostsdkdemo.host;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.applog.AppLog;
import com.u3d.appwithhostsdkdemo.ILoginCallback;
import com.u3d.appwithhostsdkdemo.ILoginServiceInterface;
import com.u3d.appwithhostsdkdemo.MainActivity;
import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.ad.AdManager;
import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.appwithhostsdkdemo.host.injected.DemoHistorySaver;
import com.u3d.appwithhostsdkdemo.host.injected.DemoLogger;
import com.u3d.appwithhostsdkdemo.login.LoginMockSDK;
import com.u3d.appwithhostsdkdemo.login.LoginService;
import com.u3d.appwithhostsdkdemo.mockAd.MockAdManager;
import com.u3d.appwithhostsdkdemo.util.WebViewCompat;
import com.u3d.webglhost.log.LogLevel;
import com.u3d.webglhost.runtime.HostError;
import com.u3d.webglhost.runtime.MiniGameLaunchOption;
import com.u3d.webglhost.runtime.TJConstants;
import com.u3d.webglhost.runtime.TJException;
import com.u3d.webglhost.runtime.TJHost;
import com.u3d.webglhost.toolkit.TJHostHandle;
import com.u3d.webglhost.toolkit.TJRuntimeConstants;
import com.u3d.webglhost.toolkit.listener.OnTJHostCreateHandleListener;
import com.u3d.webglhost.toolkit.listener.OnTJHostGamePackageDownloadHandleListener;
import com.u3d.webglhost.toolkit.listener.OnTJHostHandleInitializeListener;
import com.u3d.webglhost.toolkit.listener.OnTJHostHandleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HostContainerActivity extends AppCompatActivity {
    private static final String TAG = "[HostContainerActivity]";
    AdManager adManager;
    MockAdManager mockAdManager;
    private Object mTag;
    private TJHostHandle hostHandle;
    private FrameLayout gameContainer;
    private View gameView;
    private boolean isGameStarted;
    private boolean isGamePlayed;

    // Login AIDL
    private ILoginServiceInterface loginService;
    private ServiceConnection loginServiceConnection;
    // History Saver
    private DemoHistorySaver historySaver;
    // UI Handler
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Game Info
    String launchKey = null;
    boolean isTempSession = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initLoginServiceConnection();
        initTitle();
        initDynamicLibIfNeeded();
        initTJHostHandle();
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
        setContentView(R.layout.activity_host_container);
        gameContainer = findViewById(R.id.gameContainer);
    }

    private void initLoginServiceConnection() {
        loginServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("[initLoginService]", "onServiceConnected");
                loginService = ILoginServiceInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("[initLoginService]", "onServiceDisconnected");
                loginService = null;
            }
        };

        Intent intent = new Intent(this, LoginService.class);
        intent.setPackage(getPackageName());
        bindService(intent, loginServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initTitle() {
        String dynamicTitle = getIntent().getStringExtra("title");
        if (dynamicTitle == null) {
            return;
        }

        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                dynamicTitle);
        setTaskDescription(taskDescription);
    }

    private void initDynamicLibIfNeeded() {
        if (MainActivity.isStaticHostAar) {
            return;
        }

        String nativeLibraryPath = getIntent().getStringExtra("v8LibPath");
        Log.i("[MapleLeaf]", "[MapleLeaf] DynamicLoadingSample nativeLibraryPath: " + nativeLibraryPath);
        boolean isLoaded = TJHost.isHostNativeLibraryLoaded(this);
        Log.i("[MapleLeaf]",
                "[MapleLeaf] Sample before calling installNativeLibraryPath, isHostNativeLibraryLoaded=" + isLoaded);
        boolean result = TJHost.installNativeLibraryPath(getApplicationContext(), nativeLibraryPath);
        Log.i("[MapleLeaf]", "[MapleLeaf] DynamicLoadingSample installNativeLibraryPath result=" + result);
        isLoaded = TJHost.isHostNativeLibraryLoaded(this);
        Log.i("[MapleLeaf]",
                "[MapleLeaf] DynamicLoadingSample after calling installNativeLibraryPath isHostNativeLibraryLoaded="
                        + isLoaded);
    }

    private void initTJHostHandle() {
        Context context = this;
        PropertiesManager.init(context);
        PropertiesManager p = PropertiesManager.getInstance();
        TJHostHandle.initialize(context, p.getProperty("app.key"), p.getProperty("app.secret"), gameContainer,
                new OnTJHostHandleInitializeListener() {
                    @Override
                    public void onSuccess(TJHostHandle hostHandle) {
                        handler.post(() -> initGame());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        handler.post(() -> Toast.makeText(context,
                                "TJHostHandle.initialize failed: " + throwable.getMessage(), Toast.LENGTH_SHORT)
                                .show());
                        // finishAndRemoveTask();
                    }
                });
    }

    private void initGame() {
        // Get HostHandle instance and do some initial works
        hostHandle = TJHostHandle.getInstance();

        // Set WebView Directory Suffix, processes must use different data directories
        WebViewCompat.setDataDirectorySuffix(this);

        // Set createGameHandle option, options can be referred in the API Reference
        // manual
        Bundle hostBundle = new Bundle();
        hostBundle.putString(TJConstants.HTTP_CACHE_PATH, getDefaultCachePath());
        hostBundle.putLong(TJConstants.HOST_PAUSE_DELAY_MILLIS, 100);

        // Start Game by Game Id or QR Code
        launchKey = getIntent().getStringExtra("gameId");

        hostHandle.createGameHandle(this, hostBundle, new OnTJHostCreateHandleListener() {
            @Override
            public void onSuccess(TJHostHandle result) {
                setGameStartOptions(launchKey);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Host createGameHandle failed", throwable);
                showToast(throwable.getMessage() != null ? throwable.getMessage() : "Host create game handle failure");
            }
        });
    }

    private void setGameStartOptions(String launchKey) {
        // set TAG
        mTag = System.identityHashCode(this);
        hostHandle.setTag(mTag);

        // Inject Demo's logger / history saver / privacy checker into host
        injectLogger();
        injectHistorySaver();
        injectPrivacyChecker();

        // set first frame render listener and pkg download listener
        setGameHandleListeners();

        // GameStartOptions can be referred in the API Reference manual
        Bundle bundle = new Bundle();
        Log.i("GameActivity", "LaunchKey: " + launchKey);
        bundle.putString(TJRuntimeConstants.LAUNCH_KEY, launchKey);

        // We should use AIDL here, cause we are not in main process
        // TODO we should get this by AIDL from main process
        bundle.putString(TJRuntimeConstants.USER_ID, LoginMockSDK.getUserId());

        bundle.putInt(TJConstants.API_VERSION, getIntent().getIntExtra("apiVersion", 0));
        bundle.putBoolean(TJConstants.SHOW_MENU_LAYOUT, true);
        bundle.putBoolean(TJConstants.SHOW_LOADING_LAYOUT, true);
        bundle.putBoolean(TJConstants.ENABLE_DEBUGGER, true);
        bundle.putBoolean(TJConstants.ENABLE_VCONSOLE, true);
        bundle.putBoolean(TJConstants.ENABLE_TJAPIDEBUG, false);
        bundle.putBoolean(TJConstants.ENABLE_PERFORMANCE_MONITORING, true);
        bundle.putBoolean(TJConstants.ENABLE_CPU_PROFILER, true);
        bundle.putBoolean(TJConstants.ENABLE_CSHARP_DEBUGGER, true);
        bundle.putLong(TJConstants.NETWORK_TIMEOUT, 60000);
        bundle.putString(TJConstants.BUILTIN_CUSTOM_SCRIPTS_PATH, "customScripts"); // used to override some js API in
                                                                                    // Host SDK

        bundle.putBoolean(TJRuntimeConstants.SHOW_MENU_EXPLORE_BTN, true);
        bundle.putBoolean(TJRuntimeConstants.SPLASH_CAPSULE_RESERVE_STATUS_BAR, true);
        // bundle.putBoolean(TJConstants.ENABLE_TRANSPARENT_MODE, true);
        // bundle.putString(TJRuntimeConstants.SPLASH_ICON_URL,
        // "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOuxrvcNMfGLh73uKP1QqYpKoCB0JLXiBMvA&s");
        // bundle.putString(TJRuntimeConstants.SPLASH_GAME_TITLE, "外部传入的 Title");
        // bundle.putBoolean(TJConstants.ENABLE_INSPECTOR, true);
        // bundle.putBoolean(TJConstants.INSPECTOR_WAIT_FOR_INSPECT, false);
        // bundle.putString(TJConstants.INSPECTOR_HOST, "127.0.0.1");
        // bundle.putInt(TJConstants.INSPECTOR_PORT, 9229);
        // bundle.putFloat(TJConstants.PIXEL_RATIO, 1.0f);
        // bundle.putString(TJConstants.GAME_ENTRY_JS, "customEntry.js");

        SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = tempDate.format(new java.util.Date());
        MiniGameLaunchOption launchOptions = new MiniGameLaunchOption.Builder()
                .scene(201001)
                .shareTicket("Share 201001")
                .addQueryParameter("giftKey", "Mock Gift Key in launch options")
                .addQueryParameter("time", datetime)
                .build();

        bundle.putParcelable(TJConstants.GAME_LAUNCH_OPTION, launchOptions);

        hostHandle.setGameStartOptions(bundle, new OnTJHostHandleListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Host set game start options succeeded");
                startGame();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Host set game start options failure", throwable);
            }
        });
    }

    private void startGame() {
        setCustomCommandListeners(); // Used for custom perf data upload and custom ad service

        hostHandle.start("[Demo App]", new OnTJHostHandleListener() {
            @Override
            public void onSuccess() {
                isGameStarted = true;
                Log.v(TAG, "Host start game success");
                gameView = hostHandle.getGameView();
                if (gameContainer.indexOfChild(gameView) == -1) {
                    gameContainer.addView(gameView);
                }

                gameView.requestFocus();
                playGame();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Host start game failure", throwable);
                showToast(throwable.getMessage() != null ? throwable.getMessage() : "Host start game failure");
                if (throwable instanceof TJException) {
                    TJException e = (TJException) throwable;
                    if (e.getCode() == HostError.HOST_ALREADY_CREATED.getCode()) {
                        // ignore
                        return;
                    }
                    if (e.getCode() == HostError.COMPILE_ERROR.getCode()) {
                        cleanupGameHandle();
                    }
                }
            }
        });
    }

    private void playGame() {
        // mockMiniGameCallRewardedAdAPI(this); // Mock a game is calling create
        // rewarded Ads APIs
        // mockMiniGameCallUIAPI(this); // Mock a game is calling ui APIs
        // mockMiniGameCallOptionsAPI(this); // Mock a game is calling launch options
        // APIs

        resumeGame();
    }

    private void cleanupGameHandle() {
        hostHandle.destroyGameHandle(mTag);
        hostHandle = null;
        isGameStarted = false;
        removeHostAndShowOptions();
    }

    private void pauseGame() {
        if (hostHandle == null)
            return;
        hostHandle.pause(new OnTJHostHandleListener() {
            @Override
            public void onSuccess() {
                isGamePlayed = false;
                Log.v(TAG, "Host pause game success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Host pause game failure", throwable);
                showToast(throwable.getMessage() != null ? throwable.getMessage() : "Host pause game failure");
            }
        });
    }

    private void resumeGame() {
        if (hostHandle == null)
            return;
        if (!isGameStarted)
            return;

        Log.i(TAG, "MiniGameLaunchOption");

        // Resume game
        hostHandle.play(new OnTJHostHandleListener() {
            @Override
            public void onSuccess() {
                isGamePlayed = true;
                addEnterOptions();

                Log.v(TAG, "Host play game success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Host play game failure", throwable);
                showToast(throwable.getMessage() != null ? throwable.getMessage() : "Host play game failure");
            }
        });
    }

    // If this game is resumed from the background
    private void addEnterOptions() {
        SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = tempDate.format(new java.util.Date());
        MiniGameLaunchOption enterOptions = new MiniGameLaunchOption.Builder()
                .scene(201002)
                .shareTicket("Share 201002")
                .addQueryParameter("giftKey", "Mock Gift Key in enter options")
                .addQueryParameter("time", datetime)
                .build();
        hostHandle.setEnterOptions(enterOptions);
    }

    private void stopGame() {
        if (hostHandle == null)
            return;
        hostHandle.stop("", new OnTJHostHandleListener() {
            @Override
            public void onSuccess() {
                isGameStarted = false;
                isGamePlayed = false;
                Log.v(TAG, "Host stop game success");
                removeHostAndShowOptions();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Host stop game failure", throwable);
                showToast(throwable.getMessage() != null ? throwable.getMessage() : "Host stop game failure");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGame();
        removeHostAndShowOptions();
        TJHostHandle.destroyInstance();
        if (hostHandle != null) {
            hostHandle.destroyGameHandle(mTag);
        }

        if (loginServiceConnection != null) {
            unbindService(loginServiceConnection);
        }
        historySaver.destroy();

        Log.i("HostSample", "-------- ending of host sample --------");
    }

    private void removeHostAndShowOptions() {
        gameContainer.removeAllViews();
        boolean isLandScape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (isLandScape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    // These listeners must be set after HostHandle.createGameHandle() finishes
    // successfully,
    // which initializes the GameHandle.
    private void setGameHandleListeners() {
        // The default action is to open a debug menu
        hostHandle.setOnTJMenuListener(view -> hostHandle.showDebugMenu());

        // The default action is to do nothing
        hostHandle.setOnTJCloseListener(view -> {
            showToast("Click Close button");
            // finishAndRemoveTask();
            onBackPressed();
        });

        // The default action is to do nothing
        hostHandle.setOnFirstFrameRenderedListener(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date(System.currentTimeMillis()));
            showToast("Host first frame rendered time: " + currentTime);
            Log.v(TAG, "Host first frame rendered time: " + currentTime);
        });

        hostHandle.setOnGamePackageDownloadListener(new OnTJHostGamePackageDownloadHandleListener() {

            @Override
            public void onStart() {
                showToast("Game package download start");
            }

            @Override
            public void onProgress(long downloadedSize, long totalSize) {
                Log.d(TAG, "Download progress, downloadedSize: " + downloadedSize + " totalSize: " + totalSize);
            }

            @Override
            public void onSuccess() {
                showToast("Game package download success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                showToast("Game package download failed");
            }

            @Override
            public void onUseCache() {
                showToast("Game package use cache");
            }
        });
    }

    private String getDefaultCachePath() {
        String internalAppDataPath = "";
        File file = getApplicationContext().getDataDir();
        if (file != null) {
            internalAppDataPath = file.getAbsolutePath();
        }
        return "default: " + internalAppDataPath + "/cache";
    }

    // Inject your own logger into Host SDk
    private void injectLogger() {
        DemoLogger logger = new DemoLogger();
        hostHandle.initLog(LogLevel.ALL, logger);
    }

    // Inject your own history saver into Host SDK
    private void injectHistorySaver() {
        historySaver = new DemoHistorySaver(this);
        hostHandle.setHistorySaver(historySaver);
    }

    // Inject your own privacy checker into Host SDK
    private void injectPrivacyChecker() {
        hostHandle.setPrivacyChecker(null);
    }

    // CustomCommand is initialized in HostHandle.start(),
    // so listeners must be set after HostHandle.start() finishes successfully.
    //
    // Be careful that some AD SDK may creates a Activity when creating,
    // causing this Activity lost focus and get focus in a short time,
    // triggering the onPause and onResume callbacks.
    private void setCustomCommandListeners() {
        // initPerfReportListener();
        // initAdListener();
        initMockAdListener();
        initAuthListener();
    }

    // Override cusObj.reportPerformanceImplement in customScripts/perf.js to make
    // the host
    // call the Demo App's func "uploadPerf" when trying to upload perf data.
    // Demo App add a listener to listen "uploadPerf" method call here, to upload
    // perf data by
    // DataFinder SDK(AppLog.onEventV3).
    private void initPerfReportListener() {
        hostHandle.setCustomCommandListener("uploadPerf", (customCommandHandle, bundle) -> {
            String perf_data = bundle.getString("value0");
            try {
                JSONObject perf_json = new JSONObject(perf_data);
                AppLog.onEventV3("upload_perf", perf_json);
                customCommandHandle.success();
            } catch (JSONException e) {
                customCommandHandle.fail(100, e.getMessage());
            }
        });
    }

    // Override tj.createRewardedVideoAd in customScripts/ad.js to make the host
    // call Demo App's func
    // "loadRewardAd" and "showRewardAd" when mini game developers are trying to
    // create a rewarded Ad.
    // Demo App add listener to listen "loadRewardAd" and "showRewardAd" method call
    // here, to create
    // ad by ToBid Ad SDK.
    private void initAdListener() {
        if (adManager == null) {
            adManager = new AdManager(this);
        }

        adManager.initAds(hostHandle);

        hostHandle.setCustomCommandListener("loadRewardAd", ((customCommandHandle, bundle) -> {
            if (isGamePlayed) {
                adManager.loadRewardAd();
            }
        }));
        hostHandle.setCustomCommandListener("showRewardAd", ((customCommandHandle, bundle) -> {
            if (isGamePlayed) {
                adManager.playRewardAd();
            }
        }));
    }

    private void initMockAdListener() {
        if (mockAdManager == null) {
            mockAdManager = new MockAdManager(this);
        }

        mockAdManager.initRewardAd(hostHandle);

        hostHandle.setCustomCommandListener("loadRewardAd", ((customCommandHandle, bundle) -> {
            if (isGamePlayed) {
                mockAdManager.loadRewardAd();
            }
        }));
        hostHandle.setCustomCommandListener("showRewardAd", ((customCommandHandle, bundle) -> {
            if (isGamePlayed) {
                mockAdManager.playRewardAd();
            }
        }));
    }

    private void initAuthListener() {
        hostHandle.setCustomCommandListener("loginUnity", ((customCommandHandle, bundle) -> {
            try {
                loginService.requesetLogin(new ILoginCallback.Stub() {
                    @Override
                    public void loginEnd(String code) {
                        JSONObject loginRes = new JSONObject();
                        try {
                            loginRes.put("code", code);
                        } catch (Exception e) {
                            customCommandHandle.fail(1002, "Failed to serialize login result" + e.getMessage());
                            Log.e("initAuthListener", Objects.requireNonNull(e.getMessage()));
                        }

                        customCommandHandle.pushResult(loginRes.toString());
                        customCommandHandle.success();
                    }
                });
            } catch (RemoteException e) {
                customCommandHandle.fail(1001, "Failed to connect Login Service: " + e.getMessage());
            }
        }));
    }

    // Read a mock game js script testScripts/mock_ad.js, which simulates the mini
    // game developers
    // to call the rewarded ad APIs. And use CustomCommand.runCustomScript() to run
    // this script 20s
    // after the game is played.
    private void mockMiniGameCallRewardedAdAPI(Context context) {
        mockMiniGameCallAPI(context, "testScripts/mock_ad.js");
    }

    private void mockMiniGameCallUIAPI(Context context) {
        mockMiniGameCallAPI(context, "testScripts/mock_ui.js");
    }

    private void mockMiniGameCallOptionsAPI(Context context) {
        mockMiniGameCallAPI(context, "testScripts/mock_option.js");
    }

    private void mockMiniGameCallAPI(Context context, String fileName) {
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            String fileContent = readAssetFile(context, fileName);
            hostHandle.runCustomScript(fileContent, null);
        }, 5000);
    }

    public static String readAssetFile(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void showToast(CharSequence msg) {
        if (msg == null) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            moveTaskToBack(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent() == intent) {
            Log.i("SingleProcess", "a same Intent");
            return;
        }

        setIntent(intent);
        String newLaunchKey = getIntent().getStringExtra("gameId");
        boolean newIsTempSession = getIntent().getBooleanExtra("isTempSession", false);
        Log.i("SingleProcess", "newLaunchKey: " + newLaunchKey + "launchKey" + launchKey);

        if (Objects.equals(newLaunchKey, launchKey) && newIsTempSession == isTempSession) {
            return;
        }

        finish();
        startActivity(intent);
    }

}
