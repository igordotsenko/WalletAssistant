package com.kindhomeless.wa.walletassistant.model;

/**
 * Represents the specific place where payment was made (e.g. Kyiv Metropoliten)
 */
public class PaymentPlace {
    private String name;
    private Category associatedCategory;

    public PaymentPlace(String name, Category associatedCategory) {
        this.name = name;
        this.associatedCategory = associatedCategory;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the payment category that corresponds to the given Payment Place (e.g.
     * Kyiv Metropoliten -> public transport)
     */
    public Category getAssociatedCategory() {
        return associatedCategory;
    }
}
