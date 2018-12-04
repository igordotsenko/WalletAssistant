package com.kindhomeless.wa.walletassistant;

import android.app.Application;

import com.activeandroid.ActiveAndroid;


public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
