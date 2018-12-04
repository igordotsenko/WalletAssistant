package com.kindhomeless.wa.walletassistant.repo.storage;

import com.activeandroid.query.Select;
import com.kindhomeless.wa.walletassistant.model.Category;

class CategoryRepoImpl extends GeneralRepoImpl<Category> implements CategoryRepo {

    public CategoryRepoImpl() {
        super(Category.class);
    }

    @Override
    public Category findCategoryByName(String categoryName) {
        return new Select().from(Category.class)
                .where(Category.NAME_COLUMN + " = ?", categoryName)
                .executeSingle();
    }

    @Override
    public Category finaByWalletId(String walletId) {
        return new Select().from(Category.class)
                .where(Category.WALLET_ID_COLUMN + " = ?", walletId)
                .executeSingle();
    }
}
