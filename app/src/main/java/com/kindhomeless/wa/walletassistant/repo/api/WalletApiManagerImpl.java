package com.kindhomeless.wa.walletassistant.repo.api;

import android.content.Context;

import com.kindhomeless.wa.walletassistant.logic.credentials.Credentials;
import com.kindhomeless.wa.walletassistant.logic.credentials.CredentialsManager;
import com.kindhomeless.wa.walletassistant.logic.credentials.EmptyCredentialsException;
import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.Record;
import com.kindhomeless.wa.walletassistant.util.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Callback;

public class WalletApiManagerImpl implements WalletApiManager {
    private final WalletApiRetrofit walletApiRetrofit;
    private final CredentialsManager credentialsManager;
    private final WeakReference<Context> context;

    public WalletApiManagerImpl(Context context) {
        this.walletApiRetrofit = WalletApiClient.getInstance().getRetrofitClient();
        this.credentialsManager = CredentialsManager.getInstance();
        this.context = new WeakReference<>(context);
    }

    public void listAllCategories(Callback<List<Category>> callback) {
        Credentials credentials = retrieveCredentials();
        if (credentials != null) {
            walletApiRetrofit
                    .listAllCategories(credentials.getEmail(), credentials.getToken())
                    .enqueue(callback);
        }
    }

    public void postRecord(List<Record> record, Callback<Void> callback) {
        Credentials credentials = retrieveCredentials();
        if (credentials != null) {
            walletApiRetrofit
                    .postRecord(credentials.getEmail(), credentials.getToken(), record)
                    .enqueue(callback);
        }
    }

    private Credentials retrieveCredentials() {
        try {
            return credentialsManager.getCredentials(context.get());
        } catch (EmptyCredentialsException e) {
            ToastUtils.showToastAndLogError(context.get(), "Error on credentials retrieving", e);
            return null;
        }
    }
}
