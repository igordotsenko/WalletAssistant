package com.kindhomeless.wa.walletassistant.repo.storage;

import android.support.annotation.Nullable;

import com.kindhomeless.wa.walletassistant.model.PaymentPlace;


public interface PaymentPlaceRepo {

    @Nullable
    PaymentPlace findPaymentPlaceByName(String paymentPlaceName);
}
