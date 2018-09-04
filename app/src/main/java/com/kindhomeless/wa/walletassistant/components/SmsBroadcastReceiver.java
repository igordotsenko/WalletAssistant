package com.kindhomeless.wa.walletassistant.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(APP_TAG, "In SmsBroadcastReceiver onReceive");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle == null) {
                Log.i(APP_TAG, "Bundle is null");
                return;
            }

            final Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) {
                Log.i(APP_TAG, "pdus is null");
                return;
            }

            for (Object pdu : pdus) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String senderNum = currentMessage.getDisplayOriginatingAddress();
                // TODO deharcode
                if (!"+380931842098".equals(senderNum)) {
                    Log.i(APP_TAG, "Received sms from " + senderNum + " and it's not expected number");
                    return;
                }

                String message = currentMessage.getDisplayMessageBody();
                String logMessage = "senderNum: " + senderNum + "; message: " + message;
                Log.i(APP_TAG, logMessage);
                Toast.makeText(context, logMessage, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(APP_TAG, "Exception smsReceiver", e);
        }
    }
}

