package com.u3d.appwithhostsdkdemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.u3d.appwithhostsdkdemo.host.MultiProcHostContainerActivity1;
import com.u3d.appwithhostsdkdemo.host.MultiProcHostContainerActivity2;
import com.u3d.appwithhostsdkdemo.host.MultiProcHostContainerActivity3;
import com.u3d.appwithhostsdkdemo.host.MultiProcHostContainerActivity4;
import com.u3d.appwithhostsdkdemo.host.MultiProcHostContainerActivity5;
import com.u3d.webglhost.dynamicres.TJDynamicConfig;
import com.u3d.webglhost.dynamicres.shared.OnLoadSharedLibraryListener;
import com.u3d.webglhost.runtime.TJHost;
import com.u3d.webglhost.toolkit.multiproc.MultiProcessLauncher;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static boolean isTJHostHandleInitialized = false;
    public static String v8LibPath;
    public static final int MAX_DYNAMIC_HOST_RETRY_COUNT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initUI();
        initMultiProcessLauncher();
        initDynamicHost();
    }

    private void initUI() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        if (navHostFragment == null) {
            Toast.makeText(this, "Initialize NavHostFragment failed", Toast.LENGTH_LONG).show();
            return;
        }
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    // Demo need to deliver Activity's process and class info registered in the AndroidManifest.xml
    // So that the MultiProcessLauncher in the Host Runtime SDK will determinate which Activity is
    // going to be used to launch a game.
    private void initMultiProcessLauncher() {
        Map<String, Class<?>> map = new HashMap<>();
        map.put(":hostProc1", MultiProcHostContainerActivity1.class);
        map.put(":hostProc2", MultiProcHostContainerActivity2.class);
        map.put(":hostProc3", MultiProcHostContainerActivity3.class);
        map.put(":hostProc4", MultiProcHostContainerActivity4.class);
        map.put(":hostProc5", MultiProcHostContainerActivity5.class);
        MultiProcessLauncher.configContainer(map);
    }

    private void initDynamicHost() {
//        setupDynamicLoading();

        boolean isLoaded = TJHost.isHostNativeLibraryLoaded(this);
        Log.v("[MapleLeaf]", "Application before calling TJHost.prepare isHostNativeLibraryLoaded=" + isLoaded);
        boolean isValid = TJHost.isHostNativeLibraryValid(this);
        Log.v("[MapleLeaf]", "Application before calling TJHost.prepare isHostNativeLibraryValid=" + isValid);

        prepareDynamicHostWithRetryTimes(0, MAX_DYNAMIC_HOST_RETRY_COUNT);
    }

    private void prepareDynamicHostWithRetryTimes(int cur, int total) {
        if (cur == total) {
            return;
        }
        Log.i("[MapleLeaf]", "initDynamicHost start current index: " + cur);

        TJHost.prepare(this, new OnLoadSharedLibraryListener() {
            @Override
            public void onSuccess(String s) {
                Log.i("[MapleLeaf]", "initDynamicHost succeed: " + s);
                Toast.makeText(MainActivity.this, "initDynamicHost succeed: " + s, Toast.LENGTH_LONG).show();
                v8LibPath = s;
                isTJHostHandleInitialized = true;
            }

            @Override
            public void onDownloading(int progress) {
                // update UI
                Log.i("[MapleLeaf]", "initDynamicHost progress: " + progress);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("[MapleLeaf]", "initDynamicHost failed: " + throwable.getMessage());
                Toast.makeText(MainActivity.this, "initDynamicHost failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                isTJHostHandleInitialized = false;

                prepareDynamicHostWithRetryTimes(cur + 1, total);
            }
        });
    }

    // Used for test a arr from a local file server
    private void setupDynamicLoading() {
        String cdnForTestingArm64 = "https://127.0.0.1:8000/Host_arm64-v8a.zip";
        String cdnForTestingArm32 = "https://127.0.0.1:8000/Host_armeabi-v7a.zip";
        String cdnForTestingX64 =  "https://127.0.0.1:8000/Host_x86_64.zip";

        TJDynamicConfig.Builder builder = new TJDynamicConfig.Builder();
        boolean hasTestingCdn = false;
        if (cdnForTestingArm64 != null && !cdnForTestingArm64.isEmpty()) {
            builder.setArm64CdnUrl(cdnForTestingArm64);
            hasTestingCdn = true;
        }
        if (cdnForTestingArm32 != null && !cdnForTestingArm32.isEmpty()) {
            builder.setArm32CdnUrl(cdnForTestingArm32);
            hasTestingCdn = true;
        }
        if (cdnForTestingX64 != null && !cdnForTestingX64.isEmpty()) {
            builder.setX64CdnUrl(cdnForTestingX64);
            hasTestingCdn = true;
        }

        if (hasTestingCdn) {
            TJHost.initDynamicLoading(this, builder.create());
        }
    }
}
