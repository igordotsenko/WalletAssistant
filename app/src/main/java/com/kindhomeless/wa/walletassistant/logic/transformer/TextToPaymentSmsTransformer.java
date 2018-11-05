package com.kindhomeless.wa.walletassistant.logic.transformer;

import android.support.annotation.NonNull;

import com.kindhomeless.wa.walletassistant.model.PaymentSms;

public interface TextToPaymentSmsTransformer {

    PaymentSms transform(@NonNull String text) throws TransformationException;
}
