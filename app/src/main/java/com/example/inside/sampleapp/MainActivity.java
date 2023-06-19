package com.example.inside.sampleapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    private String TAG = "[UISDK] " + MainActivity.class.getSimpleName();

    private String[] defaultDermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    ActivityResultLauncher<Intent> getResultPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(MainActivity.this)) {
                checkPermission();
            } else {
                MainApplication.getInstance().forceStop();
            }
        }
    });

    @Override
    public void onBackPressed(){
        MainApplication.getInstance().releaseUiSdk();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 다른앱 위에 그리기
            if (Settings.canDrawOverlays(MainActivity.this)) {
                for (String permission : defaultDermissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(defaultDermissions, 1);
                        break;
                    }
                }
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
                getResultPermission.launch(intent);
            }
        }
    }
}