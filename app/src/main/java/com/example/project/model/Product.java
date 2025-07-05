
package com.example.project.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String price;
    private String originalPrice;
    private int imageRes;
    private float rating;
    private String category;
    private boolean isOnSale;

    public Product(int id, String name, String price, int imageRes, float rating, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.rating = rating;
        this.category = category;
        this.isOnSale = false;
    }

    public Product(int id, String name, String price, String originalPrice, int imageRes,
                   float rating, String category, boolean isOnSale) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageRes = imageRes;
        this.rating = rating;
        this.category = category;
        this.isOnSale = isOnSale;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isOnSale() {
        return isOnSale;
    }

    public void setOnSale(boolean onSale) {
        isOnSale = onSale;
    }
}
