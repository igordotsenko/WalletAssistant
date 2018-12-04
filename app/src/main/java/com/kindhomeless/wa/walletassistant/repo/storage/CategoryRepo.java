package com.kindhomeless.wa.walletassistant.repo.storage;

import com.kindhomeless.wa.walletassistant.model.Category;

public interface CategoryRepo extends GeneralRepo<Category> {

    Category findCategoryByName(String categoryName);

    Category finaByWalletId(String walletId);
}
