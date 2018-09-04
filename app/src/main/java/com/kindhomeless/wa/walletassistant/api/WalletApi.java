package com.kindhomeless.wa.walletassistant.api;

import com.kindhomeless.wa.walletassistant.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface WalletApi {
    @Headers({
            "Content-Type:application/json",
            "X-Token:f9ac1374-80bc-4fac-913c-f74a6985d9d0",
            "X-User:dotsenko.kyiv@gmail.com"
    })
    @GET("categories")
    Call<List<Category>> listAllCategories();
}
