package com.kindhomeless.wa.walletassistant.repo.api;

import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.Record;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

interface WalletApiRetrofit {
    String CONTENT_TYPE_HEADER = "Content-Type:application/json";
    String USER_HEADER = "X-User";
    String TOKEN_HEADER = "X-Token";

    @Headers({CONTENT_TYPE_HEADER})
    @GET("categories")
    Call<List<Category>> listAllCategories(@Header(USER_HEADER) String email,
                                           @Header(TOKEN_HEADER) String token);

    @Headers({CONTENT_TYPE_HEADER})
    @POST("records-bulk")
    Call<Void> postRecord(@Header(USER_HEADER) String email,
                          @Header(TOKEN_HEADER) String token,
                          @Body List<Record> record);

}
