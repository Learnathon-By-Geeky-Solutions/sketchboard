package com.example.lostnfound.enums;

public enum Catagory {
    Documents,
    Electronics,
    Jwelleries,
    Accessories,
    Clothes,
    Mobile;

    public static Catagory[] getAllCategories() {
        return Catagory.values();
    }
}
