package com.kindhomeless.wa.walletassistant.components;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import static com.kindhomeless.wa.walletassistant.util.Constants.WALLET_API_BASE_URL;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToast;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToastAndLogDebug;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToastAndLogError;
import static java.util.Collections.singletonList;

/**
 * This activity is created from intent which is sent when payment SMS received
 */
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
            showToast(this, "No sms text extracted");
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
            Log.d(APP_TAG, "Intent is null in Post Record Activity");
            return Optional.empty();
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(APP_TAG, "Extras are null in Post Record Activity");
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
            showToast(this, errorMsg);
            Log.e(APP_TAG, errorMsg, e);
        }
    }

    private class PostRecordButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (paymentSms == null) {
                showToast(RecordPostActivity.this, "Cannot retrieve payment amount");
                Log.d(APP_TAG, "Payment sms is null in PostRecordButtonListener");
                return;
            }

            String selectedCategoryName = categoriesDropdown.getSelectedItem().toString();
            String categoryId = categoryNameToCategory.get(selectedCategoryName).getId();
            if (isEmpty(categoryId)) {
                showToast(RecordPostActivity.this, "Category is not chosen or not known");
                return;
            }
            Record record = new Record(categoryId, paymentSms.getAmount());
            walletApi.postRecord(singletonList(record)).enqueue(new PostRecordCallback());
        }
    }

    private class CategoriesListCallback implements Callback<List<Category>> {
        private static final String CANNOT_RETRIEVE_MESSAGE = "Cannot retrieve categories";

        @Override
        public void onResponse(@NonNull Call<List<Category>> call,
                               @NonNull Response<List<Category>> response) {
            if (response.isSuccessful()) {
                List<Category> categories = response.body();
                if (categories == null) {
                    Log.d(APP_TAG, "Categories == null");
                    showToast(RecordPostActivity.this, CANNOT_RETRIEVE_MESSAGE);
                    return;
                }
                Log.d(APP_TAG, "Categories: " + categories.toString());

                categoryNameToCategory = buildCategoryNameToCategoryMap(categories);
                categoriesDropdown.setAdapter(buildDropdownAdapter());
            } else {
                String message = "Response is unsuccessful: " + response.message();
                Log.d(APP_TAG, message);
                showToast(RecordPostActivity.this,
                        String.format("%s: %s", CANNOT_RETRIEVE_MESSAGE, message));
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
            showToastAndLogError(RecordPostActivity.this, "Error on Categories getting", t);
        }

        private Map<String, Category> buildCategoryNameToCategoryMap(List<Category> categories) {
            return categories.stream()
                    .filter(category -> !isEmpty(category.getName()))
                    .collect(Collectors.toMap(
                            Category::getName, c -> c,
                            (oldValue, newValue) -> newValue,
                            TreeMap::new));
        }

        private ArrayAdapter<String> buildDropdownAdapter() {
            return new ArrayAdapter<>(RecordPostActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    new ArrayList<>(categoryNameToCategory.keySet()));
        }
    }

    private class PostRecordCallback implements Callback<Void> {
        @Override
        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            String toastMsg = response.isSuccessful() ?
                    "Record was sent successfully" :
                    "Post is not successful: " + response.code() + "; " + response.message();
            showToastAndLogDebug(RecordPostActivity.this, toastMsg);
        }

        @Override
        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            showToastAndLogError(RecordPostActivity.this, "Error on record post" , t);
        }
    }

    private WalletApi buildWalletApiClient() {
        return new Retrofit.Builder()
                .baseUrl(WALLET_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WalletApi.class);
    }
}
