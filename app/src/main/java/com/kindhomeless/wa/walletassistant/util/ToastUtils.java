package com.kindhomeless.wa.walletassistant.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;

public class ToastUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToastAndLogDebug(Context context, String message) {
        Log.d(APP_TAG, message);
        showToast(context, message);
    }

    public static void showToastAndLogError(Context context, String message, Throwable t) {
        Log.e(APP_TAG, message + ": " + t.getMessage(), t);
        showToast(context, message);
    }
}
