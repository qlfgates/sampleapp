package com.example.inside.sampleapp;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.kt.gigagenie.inside.uisdk.BuildConfig;
import com.kt.gigagenie.inside.uisdk.SdkEvent;
import com.kt.gigagenie.inside.uisdk.UiSdk;
import com.kt.gigagenie.inside.uisdk.UiSdkEventListener;

import org.json.JSONObject;

import timber.log.Timber;

public class MainApplication extends Application {

    private String TAG = "[UISDK] " + MainApplication.class.getSimpleName();

    private static MainApplication INSTANCE;
    public static UiSdk uiSdk;

    public static MainApplication getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainApplication();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        // ui sdk 초기화
        initUiSdk();

        // 로그 설정
//        if(BuildConfig.DEBUG){
//            Timber.plant(new Timber.DebugTree());
//        }

        super.onCreate();
    }

    @Override
    public void onTerminate() {
        releaseUiSdk();
        super.onTerminate();
    }

    private class EventListener implements UiSdkEventListener {
        @Override
        public void onEventFromUiSdk(JSONObject obj) {
            try {
                switch (obj.optInt("eventType")) {
                    case SdkEvent.REGISTER_UISDK:
                        // ui sdk 서비스 연결
                        break;
                    case SdkEvent.UNREGISTER_UISDK:
                        // ui sdk 서비스 해제
                        forceStop();
                        break;
                    default:
                        break;
                }
            } catch (Exception je) {
                Timber.tag(TAG).e("onEventFromUiSdk >> Error: " + je.getMessage());
            }
        }
    }

    private void initUiSdk() {
        uiSdk = UiSdk.getInstance();
        if(uiSdk.getStatus()==UiSdk.STATUS.IDLE) uiSdk.init(getApplicationContext(), getPackageName());
        uiSdk.setUiSdkEventHandler(new EventListener());
    }

    public void forceStop() {
        new Handler(Looper.getMainLooper()).post(() -> {
            System.exit(0);

            try {
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Exception e) {
                Timber.tag(TAG).e("forceStop >> Error: " + e.getMessage());
            }
        });
    }

    public void releaseUiSdk() {
        if(uiSdk != null) {
            uiSdk.release();
        }
    }
}
