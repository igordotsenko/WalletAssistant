package com.kindhomeless.wa.walletassistant.api;

import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.Record;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface WalletApi {
    String CONTENT_TYPE_HEADER = "Content-Type:application/json";
    String TOKEN_HEADER = "X-Token:f9ac1374-80bc-4fac-913c-f74a6985d9d0";
    String USER_HEADER = "X-User:dotsenko.kyiv@gmail.com";

    @Headers({CONTENT_TYPE_HEADER, TOKEN_HEADER, USER_HEADER})
    @GET("categories")
    Call<List<Category>> listAllCategories();

    @Headers({CONTENT_TYPE_HEADER, TOKEN_HEADER, USER_HEADER})
    @POST("records-bulk")
    Call<Void> postRecord(@Body List<Record> record);
}
