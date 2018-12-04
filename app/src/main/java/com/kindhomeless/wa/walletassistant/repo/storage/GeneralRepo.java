package com.kindhomeless.wa.walletassistant.repo.storage;

import com.kindhomeless.wa.walletassistant.model.PersistableModel;

import java.util.List;

public interface GeneralRepo<T extends PersistableModel> {

    long save(T persistableModel);

    void saveAll(List<T> persisatableModels);

    /**
     * Find by persistence id (not wallet id!)
     */
    T findById(long id);

    List<T> findAll();

    void deleteAll();
}
