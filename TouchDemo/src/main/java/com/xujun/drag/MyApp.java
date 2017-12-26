package com.xujun.drag;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * 717219917@qq.com      2017/12/26  10:42
 */

public class MyApp extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);//调试工具

    }



}
