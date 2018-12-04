package com.kindhomeless.wa.walletassistant.repo.storage;

@SuppressWarnings("unused")
public interface CategoryRepo {

    String findCategoryNameByPaymentPlace(String paymentPlaceName);
}
