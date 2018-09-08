package com.kindhomeless.wa.walletassistant.model;

public class PaymentSms {
    private final double amount;

    public PaymentSms(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
