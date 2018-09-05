package com.kindhomeless.wa.walletassistant.components;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.kindhomeless.wa.walletassistant.R;
import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.PaymentSms;
import com.kindhomeless.wa.walletassistant.model.Record;
import com.kindhomeless.wa.walletassistant.repo.WalletApi;
import com.kindhomeless.wa.walletassistant.util.transformer.TextToPaymentSmsTransformer;
import com.kindhomeless.wa.walletassistant.util.transformer.TextToPaymentSmsTransformerImpl;
import com.kindhomeless.wa.walletassistant.util.transformer.TransformationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.TextUtils.isEmpty;
import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;
import static com.kindhomeless.wa.walletassistant.util.Constants.SMS_TEXT_EXTRA;
import static com.kindhomeless.wa.walletassistant.util.UiUtils.showToastMessage;
import static java.util.Collections.singletonList;

public class RecordPostActivity extends AppCompatActivity {
    private TextView paymentAmountTextView;
    private WalletApi walletApi;
    private TextToPaymentSmsTransformer textToPaymentSmsTransformer;
    private Map<String, Category> categoryNameToCategory;
    private Spinner categoriesDropdown;
    private PaymentSms paymentSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_post);
        paymentAmountTextView = findViewById(R.id.payment_amount_text_view);

        textToPaymentSmsTransformer = new TextToPaymentSmsTransformerImpl();
        Optional<String> smsText = getSmsTextFromIntent();
        if (smsText.isPresent()) {
            setPaymentAmount(smsText.get());
            ((TextView) findViewById(R.id.sms_message_text_view)).setText(smsText.get());
        } else {
            showToastMessage(this, "No sms text extracted");
            return;
        }

        categoriesDropdown = findViewById(R.id.categories_list_dropdown);
        findViewById(R.id.post_record_button).setOnClickListener(new PostRecordButtonListener());
        walletApi = buildWalletApiClient();
        walletApi.listAllCategories().enqueue(new CategoriesListCallback());
    }

    private Optional<String> getSmsTextFromIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            Log.d(APP_TAG, "Intent is null in MainActivity");
            return Optional.empty();
        }

        Bundle extras = intent.getExtras();

        if (extras == null) {
            Log.d(APP_TAG, "Extras are null in MainActivity");
            return Optional.empty();
        }

        return Optional.ofNullable(extras.getString(SMS_TEXT_EXTRA));
    }

    private void setPaymentAmount(String smsText) {
        try {
            paymentSms = textToPaymentSmsTransformer.transform(smsText);
            paymentAmountTextView.setText(String.format("%s", paymentSms.getAmount()));
        } catch (TransformationException e) {
            String errorMsg = "Cannot set amount: " + e.getMessage();
            showToastMessage(this, errorMsg);
            Log.e(APP_TAG, errorMsg, e);
        }
    }

    private class PostRecordButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (paymentSms == null) {
                showToastMessage(RecordPostActivity.this, "Cannot retrieve payment amount");
                return;
            }

            String selectedCategoryName = categoriesDropdown.getSelectedItem().toString();
            String categoryId = categoryNameToCategory.get(selectedCategoryName).getId();
            if (TextUtils.isEmpty(categoryId)) {
                showToastMessage(RecordPostActivity.this, "Category is not chosen or not known");
                return;
            }
            Record record = new Record(categoryId, paymentSms.getAmount());
            walletApi.postRecord(singletonList(record)).enqueue(new PostRecordCallback());
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

                Map<String, Category> categoryNameToCategoryTemp = categories.stream()
                        .filter(category -> !isEmpty(category.getName()))
                        .collect(Collectors.toMap(Category::getName, c -> c));
                categoryNameToCategory = new TreeMap<>(categoryNameToCategoryTemp);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RecordPostActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new ArrayList<>(categoryNameToCategory.keySet()));

                categoriesDropdown.setAdapter(adapter);
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
            showToastMessage(RecordPostActivity.this, toastMsg);
        }

        @Override
        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            String message = "Error on record post: " + t.getMessage();
            Log.e(APP_TAG, message, t);
            showToastMessage(RecordPostActivity.this, message);
        }
    }

    private WalletApi buildWalletApiClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.budgetbakers.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WalletApi.class);
    }
}
