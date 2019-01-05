package com.kindhomeless.wa.walletassistant.components;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.kindhomeless.wa.walletassistant.MainActivity;
import com.kindhomeless.wa.walletassistant.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;
import static com.kindhomeless.wa.walletassistant.util.Constants.CHANNEL_ID;
import static com.kindhomeless.wa.walletassistant.util.Constants.SMS_TEXT_EXTRA;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToastAndLogError;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private final Random random = new Random();
    private final Set<String> permittedSmsSenderNumbers = new HashSet<String>() {{
       add("+380931842098");
       add("10901");
    }};

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(APP_TAG, "In SmsBroadcastReceiver onReceive");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle == null) {
                Log.d(APP_TAG, "Bundle is null");
                return;
            }

            final Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) {
                Log.d(APP_TAG, "pdus is null");
                return;
            }

            for (Object pdu : pdus) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String senderNum = currentMessage.getDisplayOriginatingAddress();
                if (!permittedSmsSenderNumbers.contains(senderNum)) {
                    Log.i(APP_TAG, "Received sms from " + senderNum + " and it's not expected number");
                    return;
                }

                String message = currentMessage.getDisplayMessageBody();
                String logMessage = "senderNum: " + senderNum + "; message: " + message;
                Log.i(APP_TAG, logMessage);
                sendPaymentToRecordNotification(context, message);
            }
        } catch (Exception e) {
            showToastAndLogError(context, "Exception in smsReceiver", e);
        }
    }

    private void sendPaymentToRecordNotification(Context context, String smsText) {
        NotificationCompat.Builder notificationBuilder = prepareNotificationBuilder(context, smsText);
        notificationBuilder.setContentIntent(buildPendingIntent(context, smsText));

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(getRandomNotificationId(), notificationBuilder.build());
        } else {
            Log.d(APP_TAG, "Got notificationManager as null in SmsBroadcastReceiver");
        }
    }

    private NotificationCompat.Builder prepareNotificationBuilder(Context context, String smsText) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(smsText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(smsText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /**
     * @return pending intent which contains sms text in extras and is used for PostRecordActivity
     * creation when notification is clicked
     */
    private PendingIntent buildPendingIntent(Context context, String smsText) {
        // Put extras
        Intent resultIntent = new Intent(context, RecordPostActivity.class);
        resultIntent.putExtra(SMS_TEXT_EXTRA, smsText);

        // Build stack builder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        // Use random int for request code to make every notification unique
        // and avoid notification overriding
        return PendingIntent.getActivity(context, random.nextInt(), resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private int getRandomNotificationId() {
        return random.nextInt(9999 - 1000) + 1000;
    }
}

