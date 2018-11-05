package com.kindhomeless.wa.walletassistant.repo.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


class WalletApiClient {
    private static final String WALLET_API_BASE_URL = "https://api.budgetbakers.com/api/v1/";
    private static final WalletApiClient instance = new WalletApiClient();
    private WalletApiRetrofit apiClient;

    static WalletApiClient getInstance() {
        return instance;
    }

    private WalletApiClient() {
        apiClient = buildWalletApiClient();
    }

    public WalletApiRetrofit getRetrofitClient() {
        return apiClient;
    }

    private static WalletApiRetrofit buildWalletApiClient() {
        return new Retrofit.Builder()
                .baseUrl(WALLET_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WalletApiRetrofit.class);
    }
}
