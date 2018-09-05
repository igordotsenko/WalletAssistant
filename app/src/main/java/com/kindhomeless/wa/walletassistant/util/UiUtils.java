package com.kindhomeless.wa.walletassistant.util;

import android.content.Context;
import android.widget.Toast;

public class UiUtils {

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
