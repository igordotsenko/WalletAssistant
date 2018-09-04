package com.kindhomeless.wa.walletassistant.components;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;

public class SmsListenerService extends Service {
    private static final String INTENT_ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private volatile SmsBroadcastReceiver smsBroadcastReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        Log.d(APP_TAG, "Started SmsListenerService");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
        Log.d(APP_TAG, "Stopped SmsListenerService");
    }

    private void registerReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_ACTION_SMS_RECEIVED);
        registerReceiver(smsBroadcastReceiver, intentFilter);
        Log.d(APP_TAG, "Registered SmsBroadcastReceiver");
    }

    private void unRegisterReceiver() {
        if (smsBroadcastReceiver != null) {
            unregisterReceiver(smsBroadcastReceiver);
            Log.d(APP_TAG, "Unregistered SmsBroadcastReceiver");
        } else {
            Log.d(APP_TAG, "SmsBroadcastReceiver in null; Cannot unregister");
        }
    }
}
