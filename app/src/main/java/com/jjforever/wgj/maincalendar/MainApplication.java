package com.jjforever.wgj.maincalendar;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by Wgj on 2016/10/28.
 * 自定义的Application
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        enabledStrictMode();
    }

    private void enabledStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
