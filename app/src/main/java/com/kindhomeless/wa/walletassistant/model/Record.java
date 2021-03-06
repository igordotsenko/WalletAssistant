package com.kindhomeless.wa.walletassistant.model;


import com.kindhomeless.wa.walletassistant.logic.transformer.AccountMapper;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Record {
    private static final String DEFAULT_CATEGORY_ID = "9a1dce68-2416-4b3a-898d-cc2f7124f70c"; // Unknown
    private static final String DEFAULT_ACCOUNT_ID = AccountMapper.DEFAULT_ACCOUNT_ID;
    private static final String DEFAULT_CURRENCY = "40b4a897-dcc4-49b9-9e52-76898d852b35"; // UAH
    private static final String DEFAULT_PAYMENT_TYPE = "debit_card";
    private static final double DEFAULT_AMOUNT = 0.01;

    private String categoryId;
    private String accountId;
    private String currencyId;
    private double amount;
    private String paymentType;

    @SuppressWarnings("WeakerAccess")
    public Record(String categoryId, String accountId, String currencyId,
                  double amount, String paymentType) {
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.currencyId = currencyId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public Record() {
        this(DEFAULT_CATEGORY_ID, DEFAULT_ACCOUNT_ID, DEFAULT_CURRENCY, DEFAULT_AMOUNT, DEFAULT_PAYMENT_TYPE);
    }

    public Record(String categoryId, String accountId, double amount) {
        this(categoryId, accountId, DEFAULT_CURRENCY, amount, DEFAULT_PAYMENT_TYPE);
    }
}
