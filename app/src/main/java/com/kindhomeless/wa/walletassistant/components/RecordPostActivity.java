package com.kindhomeless.wa.walletassistant.components;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.kindhomeless.wa.walletassistant.R;
import com.kindhomeless.wa.walletassistant.logic.transformer.TextToPaymentSmsTransformer;
import com.kindhomeless.wa.walletassistant.logic.transformer.TextToPaymentSmsTransformerImpl;
import com.kindhomeless.wa.walletassistant.logic.transformer.TransformationException;
import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.PaymentPlace;
import com.kindhomeless.wa.walletassistant.model.PaymentSms;
import com.kindhomeless.wa.walletassistant.model.Record;
import com.kindhomeless.wa.walletassistant.repo.api.WalletApiManager;
import com.kindhomeless.wa.walletassistant.repo.api.WalletApiManagerImpl;
import com.kindhomeless.wa.walletassistant.repo.storage.PaymentPlaceRepo;
import com.kindhomeless.wa.walletassistant.repo.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;
import static com.kindhomeless.wa.walletassistant.util.Constants.APP_TAG;
import static com.kindhomeless.wa.walletassistant.util.Constants.SMS_TEXT_EXTRA;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToast;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToastAndLogDebug;
import static com.kindhomeless.wa.walletassistant.util.ToastUtils.showToastAndLogError;
import static java.util.Collections.singletonList;

/**
 * This activity is created from intent which is sent when payment SMS received
 */
public class RecordPostActivity extends AppCompatActivity {
    private TextView paymentAmountTextView;
    private TextView paymentPlaceTextView;
    private TextView suggestedCategoryTextView;
    private Spinner categoriesDropdown;
    private TextView smsMessageTextView;
    private Button postRecordButton;

    private WalletApiManager walletApiManager;
    private TextToPaymentSmsTransformer textToPaymentSmsTransformer;
    private Map<String, Category> categoryNameToCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_post);
        initializeUiElements();

        PaymentPlaceRepo paymentPlaceRepo
                = RepositoryManager.getInstance().getRepository(PaymentPlaceRepo.class);
        textToPaymentSmsTransformer = new TextToPaymentSmsTransformerImpl(paymentPlaceRepo);

        Optional<String> smsText = getSmsTextFromIntent();
        if (!smsText.isPresent()) {
            showToast(this, "No sms text extracted");
            return;
        }

        Optional<PaymentSms> paymentSms = buildPaymentSms(smsText.get());
        if (!paymentSms.isPresent()) {
            return;
        }

        handlePaymentSms(paymentSms.get());
        walletApiManager = new WalletApiManagerImpl(this);
        walletApiManager.listAllCategories(new CategoriesListCallback(paymentSms.get()));
    }

    private void initializeUiElements() {
        paymentAmountTextView = findViewById(R.id.payment_amount_text_view);
        paymentPlaceTextView = findViewById(R.id.payment_place_text_view);
        suggestedCategoryTextView = findViewById(R.id.suggested_category_text_view);
        categoriesDropdown = findViewById(R.id.categories_list_dropdown);
        smsMessageTextView = findViewById(R.id.sms_message_text_view);
        postRecordButton = findViewById(R.id.post_record_button);
    }

    private void handlePaymentSms(PaymentSms paymentSms) {
        paymentAmountTextView.setText(String.format("%s", paymentSms.getAmount()));
        smsMessageTextView.setText(paymentSms.getText());
        postRecordButton.setOnClickListener(new PostRecordButtonListener(paymentSms));
        paymentSms.getPaymentPlace().ifPresent(this::handlePaymentPlace);
    }

    private void handlePaymentPlace(PaymentPlace paymentPlace) {
        paymentPlaceTextView.setText(paymentPlace.getName());
        suggestedCategoryTextView.setText(paymentPlace.getAssociatedCategory().getName());
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

    private Optional<PaymentSms> buildPaymentSms(String smsText) {
        try {
            return Optional.of(textToPaymentSmsTransformer.transform(smsText));
        } catch (TransformationException e) {
            String errorMsg = "Cannot transform payment sms: " + e.getMessage();
            showToast(this, errorMsg);
            Log.e(APP_TAG, errorMsg, e);
            return Optional.empty();
        }
    }

    private class PostRecordButtonListener implements View.OnClickListener {
        private final PaymentSms paymentSms;

        public PostRecordButtonListener(PaymentSms paymentSms) {
            this.paymentSms = paymentSms;
        }

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
            walletApiManager.postRecord(singletonList(record), new PostRecordCallback());
        }
    }

    private class CategoriesListCallback implements Callback<List<Category>> {
        private static final String CANNOT_RETRIEVE_MESSAGE = "Cannot retrieve categories";
        private PaymentSms paymentSms;

        public CategoriesListCallback(PaymentSms paymentSms) {
            this.paymentSms = paymentSms;
        }

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
                paymentSms.getPaymentPlace().ifPresent(this::selectSuggestedCategoryOnDropdown);
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

        @SuppressWarnings("unchecked")
        private void selectSuggestedCategoryOnDropdown(PaymentPlace paymentPlace) {
            int position = ((ArrayAdapter) categoriesDropdown.getAdapter())
                    .getPosition(paymentPlace.getAssociatedCategory().getName());
            categoriesDropdown.setSelection(position);
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
}
