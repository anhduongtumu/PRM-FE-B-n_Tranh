package com.example.project.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String productName;
    private String briefDescription;
    private String fullDescription;
    private String technicalSpecifications;
    private double price;
    private String originalPrice;
    private String imageURL;
    private int categoryID;
    private Category category;
    private float rating; // Nếu API không có thì bạn có thể tính tạm hoặc bỏ
    private boolean onSale; // Nếu cần phân biệt sản phẩm giảm giá

    public Product(int i, String tranhTrừuTượngNghệThuật, String môTả, String chiTiết, String s, int i1, String url, String trừuTượng) {
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String getTechnicalSpecifications() {
        return technicalSpecifications;
    }

    public double getPrice() {
        return price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public Category getCategory() {
        return category;
    }

    public float getRating() {
        return rating;
    }

    public boolean isOnSale() {
        return onSale;
    }

    // Setter nếu cần
    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }
}
