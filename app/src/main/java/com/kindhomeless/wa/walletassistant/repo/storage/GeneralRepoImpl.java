package com.kindhomeless.wa.walletassistant.repo.storage;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.kindhomeless.wa.walletassistant.model.PersistableModel;

import java.util.List;

public class GeneralRepoImpl<T extends PersistableModel> implements GeneralRepo<T> {
    private Class<T> persistableModelClass;

    public GeneralRepoImpl(Class<T> persistableModelClass) {
        this.persistableModelClass = persistableModelClass;
    }

    @Override
    public long save(T persistableModel) {
        return persistableModel.save();
    }

    @Override
    public void saveAll(List<T> persistableModels) {
        ActiveAndroid.beginTransaction();
        try {
            persistableModels.forEach(PersistableModel::save);
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    @Override
    public T findById(long id) {
        return new Select()
                .from(persistableModelClass)
                .where(PersistableModel.RECORD_ID_COLUMN_NAME + " = ?", id)
                .executeSingle();
    }

    @Override
    public List<T> findAll() {
        return new Select().from(persistableModelClass).execute();
    }

    @Override
    public void deleteAll() {
        new Delete().from(persistableModelClass).execute();
    }
}
