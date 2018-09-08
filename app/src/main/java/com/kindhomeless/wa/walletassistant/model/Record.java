package com.kindhomeless.wa.walletassistant.model;


@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Record {
    private static final String DEFAULT_CATEGORY_ID = "9a1dce68-2416-4b3a-898d-cc2f7124f70c"; // Unknown
    private static final String DEFAULT_ACCOUNT_ID = "6deba941-7b37-42f1-83fc-627519e99a3a"; // Aval Card
    private static final String DEFAULT_CURRENCY = "40b4a897-dcc4-49b9-9e52-76898d852b35"; // UAH
    private static final String DEFAULT_PAYMENT_TYPE = "debit_card";
    private static final double DEFAULT_AMOUNT = 0.01;

    private final String categoryId;
    private final String accountId;
    private final String currencyId;
    private final double amount;
    private final String paymentType;

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

    public Record(String categoryId, double amount) {
        this(categoryId, DEFAULT_ACCOUNT_ID, DEFAULT_CURRENCY, amount, DEFAULT_PAYMENT_TYPE);
    }
}
