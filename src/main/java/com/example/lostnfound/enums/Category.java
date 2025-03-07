package com.example.lostnfound.enums;

public enum Category {
    DOCUMENTS,
    ELECTRONICS,
    JEWELLERIES,
    ACCESSORIES,
    CLOTHES,
    MOBILE;

    public static Category[] getAllCategories() {
        return Category.values();
    }
}
