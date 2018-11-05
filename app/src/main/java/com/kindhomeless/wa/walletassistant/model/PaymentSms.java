package com.kindhomeless.wa.walletassistant.model;

public class PaymentSms {
    private final String text;
    private final double amount;

    public PaymentSms(String smsText, double amount) {
        this.text = smsText;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getText() {
        return text;
    }
}
