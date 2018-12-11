package com.kindhomeless.wa.walletassistant.logic.transformer;

import java.util.HashMap;
import java.util.Map;

// TODO: avoid hardcoding
public class AccountMapper {
    static final String AVAL_ID = "6deba941-7b37-42f1-83fc-627519e99a3a";
    static final String AVAL_CR_ID = "76614372-3183-4ae6-a131-ae39c785efca";
    public static final String DEFAULT_ACCOUNT_ID = AVAL_ID;
    private final static Map<String, String> accountInfoToAccountId = new HashMap<>();
    private final static Map<String, String> accountIdToAccountName = new HashMap<>();

    static {
        accountInfoToAccountId.put("3498", AVAL_ID);
        accountInfoToAccountId.put("2427", AVAL_CR_ID);
        accountInfoToAccountId.put("3134", AVAL_ID);

        accountIdToAccountName.put(AVAL_CR_ID, "Aval CR");
        accountIdToAccountName.put(AVAL_ID, "Aval");
    }

    public static String getAccountId(String paymentAccountInfo) {
        return accountInfoToAccountId.getOrDefault(paymentAccountInfo, DEFAULT_ACCOUNT_ID);
    }

    public static String getAccountName(String accountId) {
        return accountIdToAccountName.get(accountId);
    }
}
