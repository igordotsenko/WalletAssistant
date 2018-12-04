package com.kindhomeless.wa.walletassistant.model;

@SuppressWarnings("unused")
public class Category {
    private String id;
    private String name;
    private String color;
    private int icon;
    private String defaultType;
    private int position;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public String getId() {
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
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", icon=" + icon +
                ", defaultType='" + defaultType + '\'' +
                ", position=" + position +
                '}';
    }
}
