package com.u3d.appwithhostsdkdemo.home.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.u3d.appwithhostsdkdemo.R;
import com.u3d.appwithhostsdkdemo.config.PropertiesManager;
import com.u3d.appwithhostsdkdemo.login.LoginMockSDK;
import com.u3d.webglhost.toolkit.TJHostHandle;

public class SettingModal extends DialogFragment {
    private EditText userIdEt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set the dialog to be full-screen and remove default dialog background
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void initView(View view) {
        TextView serverUrlTv = view.findViewById(R.id.serverUrlTv);
        TextView orgNameTv = view.findViewById(R.id.orgNameTv);
        TextView runtimeSDKTv = view.findViewById(R.id.runtimeSDKTv);
        userIdEt = view.findViewById(R.id.userIdEt);

        PropertiesManager p = PropertiesManager.getInstance();
        serverUrlTv.setText(p.getProperty("app.host.server.domain"));
        orgNameTv.setText(p.getProperty("app.org.name"));
        runtimeSDKTv.setText(TJHostHandle.getCommitId());

        userIdEt.setText(LoginMockSDK.getCode());
    }

    @Override
    public void onDestroyView() {
        String userId = userIdEt.getText().toString();
        if (!userId.isEmpty()) {
            LoginMockSDK.changeUser(userId);
        }
        super.onDestroyView();
    }
}
