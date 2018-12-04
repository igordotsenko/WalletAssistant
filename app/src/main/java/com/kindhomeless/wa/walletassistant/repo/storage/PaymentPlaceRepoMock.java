package com.kindhomeless.wa.walletassistant.repo.storage;

import android.support.annotation.Nullable;

import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.PaymentPlace;

import java.util.HashMap;
import java.util.Map;

public class PaymentPlaceRepoMock implements PaymentPlaceRepo {
    public final static String METROPOLITEN_PAYMENT_NAME = "kyivskyi metropoliten";
    public final static String PUBLIC_TRANSPORT_CATEGORY_NAME = "Public transport";
    public final static String UBER_PAYMENT_NAME = "uber bv";
    public final static String UBER_CATEGORY_NAME = "Taxi";

    private final Map<String, PaymentPlace> paymentPlaceByName = new HashMap<>();

    public PaymentPlaceRepoMock() {
        initializePaymentPlaceByNameMap();
    }

    @Nullable
    @Override
    public PaymentPlace findPaymentPlaceByName(String paymentPlaceName) {
        String paymentPlaceLowerCase = paymentPlaceName.trim().toLowerCase();
        return paymentPlaceByName.get(paymentPlaceLowerCase);
    }

    private void initializePaymentPlaceByNameMap() {
        addPaymentPlaceByNameEntry(METROPOLITEN_PAYMENT_NAME, PUBLIC_TRANSPORT_CATEGORY_NAME);
        addPaymentPlaceByNameEntry(UBER_PAYMENT_NAME, UBER_CATEGORY_NAME);
    }

    private void addPaymentPlaceByNameEntry(String paymentPlaceName, String categoryName) {
        PaymentPlace paymentPlace = new PaymentPlace(paymentPlaceName, new Category(categoryName));
        paymentPlaceByName.put(paymentPlaceName, paymentPlace);
    }
}
