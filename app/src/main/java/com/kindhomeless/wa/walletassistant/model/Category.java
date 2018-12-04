package com.kindhomeless.wa.walletassistant.model;


import android.support.annotation.VisibleForTesting;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.kindhomeless.wa.walletassistant.model.PersistableModel.RECORD_ID_COLUMN_NAME;

@SuppressWarnings("unused")
@Table(name = "category", id = RECORD_ID_COLUMN_NAME)
public class Category extends PersistableModel {
    private static final String UNIQUE_NAME_AND_ID_GROUP = "unique_name_and_id";

    public static final String WALLET_ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";

    @Column(name = WALLET_ID_COLUMN,
            unique = true,
            uniqueGroups = {UNIQUE_NAME_AND_ID_GROUP},
            onUniqueConflict = Column.ConflictAction.REPLACE
    )
    private String id;

    @Column(name = NAME_COLUMN,
            unique = true,
            uniqueGroups = {UNIQUE_NAME_AND_ID_GROUP},
            onUniqueConflict = Column.ConflictAction.REPLACE,
            notNull = true
    )
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "icon")
    private int icon;

    @Column(name = "default_type")
    private String defaultType;

    @Column(name = "position")
    private int position;

    public Category() {
    }

    @VisibleForTesting
    public Category(String id, String name, String color, int icon,
                    String defaultType, int position) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.defaultType = defaultType;
        this.position = position;
    }

    public Category(String name) {
        this.name = name;
    }

    public String getWalletId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getIcon() {
        return icon;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("color", color)
                .add("icon", icon)
                .add("defaultType", defaultType)
                .add("position", position)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Category category = (Category) o;
        return icon == category.icon &&
                position == category.position &&
                Objects.equal(id, category.id) &&
                Objects.equal(name, category.name) &&
                Objects.equal(color, category.color) &&
                Objects.equal(defaultType, category.defaultType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), id, name, color, icon, defaultType, position);
    }
}
