package com.kindhomeless.wa.walletassistant;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.kindhomeless.wa.walletassistant.model.Category;

public class WalletAssistantApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration dbConfiguration = new Configuration.Builder(this)
                .setDatabaseName("wallet_assistant")
                .setDatabaseVersion(1)
                .addModelClass(Category.class)
                .create();

        ActiveAndroid.initialize(dbConfiguration);
    }
}
