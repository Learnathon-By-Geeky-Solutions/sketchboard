package com.example.lostnfound.enums;

public enum Catagory {
    DOCUMENTS,
    ELECTRONICS,
    JWELLERIES,
    ACCESSORIES,
    CLOTHES,
    MOBILE;

    public static Catagory[] getAllCategories() {
        return Catagory.values();
    }
}
