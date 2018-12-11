package com.kindhomeless.wa.walletassistant.model;

import java.util.Optional;

public class PaymentSms {
    private final String text;
    private final double amount;
    private final PaymentPlace paymentPlace;
    private final String accountId;

    public PaymentSms(String smsText, double amount, PaymentPlace paymentPlace, String accountId) {
        this.text = smsText;
        this.amount = amount;
        this.paymentPlace = paymentPlace;
        this.accountId = accountId;
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

    public String getAccountId() {
        return accountId;
    }
}
