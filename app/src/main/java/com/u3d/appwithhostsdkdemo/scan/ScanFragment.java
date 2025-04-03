package com.u3d.appwithhostsdkdemo.scan;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.u3d.appwithhostsdkdemo.MainActivity;
import com.u3d.appwithhostsdkdemo.R;
import com.u3d.webglhost.toolkit.multiproc.MultiProcessLauncher;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;

public class ScanFragment extends Fragment {
    private View mRootView;
    private EditText downloadUrlEt;
    private TextView errorTv;
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            barcodeView.pause();

            String downloadUrl = result.getText();
            beepManager.playBeepSoundAndVibrate();

            WeakReference<Activity> weakActivity = new WeakReference<>(getActivity());
            if (weakActivity.get() == null) {
                barcodeView.resume();
                return;
            }
            Activity activity = weakActivity.get();

            if (!downloadUrl.startsWith("http") && !downloadUrl.startsWith("https")) {
                Toast.makeText(activity, downloadUrl + " is not a valid url!", Toast.LENGTH_LONG).show();
                barcodeView.resume();
                return;
            }

            downloadUrlEt.setText(downloadUrl);

            if (!MainActivity.isTJHostHandleInitialized) {
                Log.e("HomeFragment", "TJHostHandle is not initialized!");
                Toast.makeText(activity, "TJHostHandle is not initialized!", Toast.LENGTH_SHORT).show();
                barcodeView.resume();
                return;
            }

            MultiProcessLauncher.launch(activity, downloadUrl, intent -> {
                intent.putExtra("v8LibPath", MainActivity.v8LibPath);
                intent.putExtra("isTempSession", true);
            });
        }
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(),
        result -> {
            if (result) {
                barcodeView.resume();
            } else {
                Toast.makeText(getContext(), "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_scan, container, false);
            initUI(mRootView);
        } else {
            if (mRootView.getParent() != null) {
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
        }
        return mRootView;
    }

    private void initUI(View view) {
        initHintArea(view);
        initScanArea(view);
    }

    private void initHintArea(View view) {
        downloadUrlEt = view.findViewById(R.id.downloadUrlEt);
        errorTv = view.findViewById(R.id.errorTv);
        errorTv.setText(null);
        errorTv.setVisibility(View.GONE);

        View playBtn = view.findViewById(R.id.playBtn);

        playBtn.setOnClickListener(v -> {
            // open game by url
        });
    }

    private void initScanArea(View view) {
        barcodeView = view.findViewById(R.id.barcodeView);
        Collection<BarcodeFormat> formats = List.of(BarcodeFormat.QR_CODE);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getActivity().getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(getActivity());
    }

    private void setErrorText(String hint) {
        errorTv.setText(hint);
        if (hint != null && !hint.isEmpty()) {
            errorTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (hasCameraPermission()) {
            barcodeView.resume();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    private boolean hasRequestedPermissionBefore = false;

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showPermissionRationale();
        } else if (!hasRequestedPermissionBefore) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            hasRequestedPermissionBefore = true;
        }
    }

    private void showPermissionRationale() {
        new AlertDialog.Builder(requireContext())
                .setTitle("需要相机权限")
                .setMessage("此功能需要相机权限来扫描二维码。")
                .setPositiveButton("允许", (dialog, which) -> requestPermissionLauncher.launch(Manifest.permission.CAMERA))
                .setNegativeButton("拒绝", null)
                .show();
    }
}
