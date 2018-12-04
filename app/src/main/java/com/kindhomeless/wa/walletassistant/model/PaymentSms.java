package com.kindhomeless.wa.walletassistant.model;

import java.util.Optional;

public class PaymentSms {
    private final String text;
    private final double amount;
    private final PaymentPlace paymentPlace;

    public PaymentSms(String smsText, double amount, PaymentPlace paymentPlace) {
        this.text = smsText;
        this.amount = amount;
        this.paymentPlace = paymentPlace;
    }

    public double getAmount() {
        return amount;
    }

    public String getText() {
        return text;
    }

    public Optional<PaymentPlace> getPaymentPlace() {
        return Optional.ofNullable(paymentPlace);
    }
}
