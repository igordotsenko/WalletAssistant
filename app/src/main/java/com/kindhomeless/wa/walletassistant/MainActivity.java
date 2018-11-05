package com.kindhomeless.wa.walletassistant;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kindhomeless.wa.walletassistant.components.SmsListenerService;
import com.kindhomeless.wa.walletassistant.logic.credentials.Credentials;
import com.kindhomeless.wa.walletassistant.logic.credentials.CredentialsManager;
import com.kindhomeless.wa.walletassistant.logic.credentials.EmptyCredentialsException;
import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.Record;
import com.kindhomeless.wa.walletassistant.repo.api.WalletApiManager;
import com.kindhomeless.wa.walletassistant.repo.api.WalletApiManagerImpl;
import com.kindhomeless.wa.walletassistant.util.ToastUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;
import static com.kindhomeless.wa.walletassistant.util.Constants.CHANNEL_ID;
import static com.kindhomeless.wa.walletassistant.util.Constants.REQUEST_CODE_ASK_PERMISSIONS;
import static java.util.Collections.singletonList;

public class MainActivity extends AppCompatActivity {
    private WalletApiManager walletApiManager;
    private TextView categoriesTextView;
    private EditText emailEditText;
    private EditText tokenEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSMSPermission(this);
        createNotificationChannel();
        walletApiManager = new WalletApiManagerImpl(this);

        initializeUiElements();
    }

    private void initializeUiElements() {
        findViewById(R.id.get_categories_button).setOnClickListener(v -> listAllCategories());
        findViewById(R.id.send_test_record_button).setOnClickListener(v -> sendTestRecord());
        findViewById(R.id.start_service_button).setOnClickListener(v -> startSmsListenerService());
        findViewById(R.id.stop_service_button).setOnClickListener(v -> stopSmsListenerService());
        findViewById(R.id.apply_credentials_button)
                .setOnClickListener(new ApplyCredentialsButtonListener());

        emailEditText = findViewById(R.id.e_mail_edit_text);
        tokenEditText = findViewById(R.id.token_edit_text);
        categoriesTextView = findViewById(R.id.categories);
    }

    private void startSmsListenerService() {
        startService(new Intent(getApplicationContext(), SmsListenerService.class));
    }

    private void stopSmsListenerService() {
        stopService(new Intent(getApplicationContext(), SmsListenerService.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestSMSPermission(@NonNull Activity activity) {
        final String permission = Manifest.permission.RECEIVE_SMS;
        int hasSpecificPermission = ContextCompat.checkSelfPermission(activity, permission);
        if (shouldRequestSmsPermission(hasSpecificPermission, activity, permission)) {
            activity.requestPermissions(new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean shouldRequestSmsPermission(int hasSpecificPermission,
                                               Activity activity,
                                               String permission) {
        return hasSpecificPermission != PackageManager.PERMISSION_GRANTED
                && !activity.shouldShowRequestPermissionRationale(permission);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence notificationChannelName = getString(R.string.notification_channel_name);
            String notificationChannelDescriptor = getString(R.string.notification_channel_description);
            int notificationImportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    notificationChannelName,
                    notificationImportance);
            channel.setDescription(notificationChannelDescriptor);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.d(APP_TAG, "notificationManager returned as null in MainActivity " +
                        "when creating a channel");
            }
        }
    }

    private class CategoriesListCallback implements Callback<List<Category>> {
        @Override
        public void onResponse(@NonNull Call<List<Category>> call,
                               @NonNull Response<List<Category>> response) {
            if (response.isSuccessful()) {
                List<Category> categories = response.body();
                if (categories == null) {
                    Log.d(APP_TAG, "Categories == null");
                    return;
                }
                Log.d(APP_TAG, "Categories: " + categories.toString());
                categoriesTextView.setText(categories.toString());
            } else {
                Log.d(APP_TAG, "Response is unsuccessful: " + response.message());
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
            Log.e(APP_TAG, "Error on Categories getting: " + t.getMessage(), t);
        }
    }

    private class PostRecordCallback implements Callback<Void> {
        @Override
        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            String toastMsg = response.isSuccessful() ?
                    "Record was sent successfully" :
                    "Post is not successful: " + response.code() + "; " + response.message();
            Log.d(APP_TAG, toastMsg);
            Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            String message = "Error on record post: " + t.getMessage();
            Log.e(APP_TAG, message, t);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void sendTestRecord() {
        walletApiManager.postRecord(singletonList(new Record()), new PostRecordCallback());
    }

    private void listAllCategories() {
        walletApiManager.listAllCategories(new CategoriesListCallback());
    }

    private class ApplyCredentialsButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                CredentialsManager.getInstance().saveCredentials(MainActivity.this, buildCredentials());
            } catch (EmptyCredentialsException e) {
                ToastUtils.showToastAndLogError(MainActivity.this, "Error on credential saving", e);
            }
        }

        private Credentials buildCredentials() throws EmptyCredentialsException {
            return new Credentials(
                    emailEditText.getText().toString(),
                    tokenEditText.getText().toString());
        }
    }
}
