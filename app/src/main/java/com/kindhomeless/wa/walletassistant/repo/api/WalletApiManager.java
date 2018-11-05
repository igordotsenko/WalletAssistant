package com.kindhomeless.wa.walletassistant.repo.api;

import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.Record;

import java.util.List;

import retrofit2.Callback;

public interface WalletApiManager {

    void listAllCategories(Callback<List<Category>> callback);

    void postRecord(List<Record> record, Callback<Void> callback);
}
