package com.example.project.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private String selectedSize;
    private String selectedColor;
    private long addedTime;

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.addedTime = System.currentTimeMillis();
    }

    public CartItem(Product product, int quantity, String selectedSize, String selectedColor) {
        this.product = product;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
        this.selectedColor = selectedColor;
        this.addedTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }

    // Helper methods
    public double getTotalPrice() {
        if (product == null) return 0.0;

        String priceString = product.getPrice();
        double price = Double.parseDouble(priceString.replaceAll("[đ.,]", ""));
        return price * quantity;
    }

    public boolean hasVariants() {
        return selectedSize != null || selectedColor != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CartItem cartItem = (CartItem) obj;

        if (product != null ? !product.equals(cartItem.product) : cartItem.product != null)
            return false;
        if (selectedSize != null ? !selectedSize.equals(cartItem.selectedSize) : cartItem.selectedSize != null)
            return false;
        return selectedColor != null ? selectedColor.equals(cartItem.selectedColor) : cartItem.selectedColor == null;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (selectedSize != null ? selectedSize.hashCode() : 0);
        result = 31 * result + (selectedColor != null ? selectedColor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", selectedSize='" + selectedSize + '\'' +
                ", selectedColor='" + selectedColor + '\'' +
                ", addedTime=" + addedTime +
                '}';
    }
}