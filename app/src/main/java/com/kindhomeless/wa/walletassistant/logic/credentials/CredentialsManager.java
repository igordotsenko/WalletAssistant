package com.kindhomeless.wa.walletassistant.logic.credentials;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class CredentialsManager {
    private static final String GLOBAL_SHARED_PREFS_NAME = "com.kindhomeless.wa.walletassistant.GLOBAL_SHARED_PREFS";
    private static final String E_MAIL_SHARED_PREFS_KEY = "credentials_email";
    private static final String TOKEN_SHARED_PREFS_KEY = "credentials_token";
    private static final CredentialsManager ourInstance = new CredentialsManager();

    public static CredentialsManager getInstance() {
        return ourInstance;
    }

    private CredentialsManager() {
    }

    public Credentials getCredentials(Context context) throws EmptyCredentialsException {
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(GLOBAL_SHARED_PREFS_NAME, MODE_PRIVATE);

        return new Credentials(
                sharedPreferences.getString(E_MAIL_SHARED_PREFS_KEY, null),
                sharedPreferences.getString(TOKEN_SHARED_PREFS_KEY, null));
    }

    public void saveCredentials(Context context, Credentials credentials) {
        context.getSharedPreferences(GLOBAL_SHARED_PREFS_NAME, MODE_PRIVATE).edit()
                .putString(E_MAIL_SHARED_PREFS_KEY, credentials.getEmail())
                .putString(TOKEN_SHARED_PREFS_KEY, credentials.getToken())
                .apply();
    }
}
