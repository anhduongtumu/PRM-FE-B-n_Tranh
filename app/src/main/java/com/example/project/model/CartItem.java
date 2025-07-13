package com.example.project.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CartItem implements Serializable {

    private int id;

    @SerializedName("cartID")
    private int cartID;

    @SerializedName("Product")
    private Product product;

    @SerializedName("Cart")
    private Cart cart;

    private double price;
    private int quantity;
    private String selectedSize;
    private String selectedColor;
    private long addedTime;

    public CartItem() {
        this.addedTime = System.currentTimeMillis();
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

    // -------------------- Getter & Setter --------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Ưu tiên lấy cartID từ Cart object nếu có
    public int getCartID() {
        return (cart != null) ? cart.getId() : cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    // -------------------- Logic phụ trợ --------------------

    public double getTotalPrice() {
        return this.price * quantity;
    }

    public boolean hasVariants() {
        return selectedSize != null || selectedColor != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CartItem)) return false;

        CartItem other = (CartItem) obj;

        if (product != null ? product.getId() != other.product.getId() : other.product != null)
            return false;
        if (selectedSize != null ? !selectedSize.equals(other.selectedSize) : other.selectedSize != null)
            return false;
        return selectedColor != null ? selectedColor.equals(other.selectedColor) : other.selectedColor == null;
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.getId() : 0;
        result = 31 * result + (selectedSize != null ? selectedSize.hashCode() : 0);
        result = 31 * result + (selectedColor != null ? selectedColor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartID=" + getCartID() +
                ", product=" + (product != null ? product.getProductName() : null) +
                ", quantity=" + quantity +
                ", selectedSize='" + selectedSize + '\'' +
                ", selectedColor='" + selectedColor + '\'' +
                ", addedTime=" + addedTime +
                '}';
    }
}
