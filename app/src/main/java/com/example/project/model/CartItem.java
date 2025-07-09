package com.example.project.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;                 // ID của CartItem (trong DB)
    private int cartID;            // ID của Cart mà item này thuộc về
    private Product product;
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

    // ----------- Getter và Setter -----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

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

    public double getTotalPrice() {
        if (product == null) return 0.0;
        return product.getPrice() * quantity;
    }

    public boolean hasVariants() {
        return selectedSize != null || selectedColor != null;
    }

    // ----------- equals, hashCode, toString -----------

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CartItem cartItem = (CartItem) obj;

        if (product != null ? product.getId() != cartItem.product.getId() : cartItem.product != null)
            return false;
        if (selectedSize != null ? !selectedSize.equals(cartItem.selectedSize) : cartItem.selectedSize != null)
            return false;
        return selectedColor != null ? selectedColor.equals(cartItem.selectedColor) : cartItem.selectedColor == null;
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
                ", cartID=" + cartID +
                ", product=" + (product != null ? product.getProductName() : null) +
                ", quantity=" + quantity +
                ", selectedSize='" + selectedSize + '\'' +
                ", selectedColor='" + selectedColor + '\'' +
                ", addedTime=" + addedTime +
                '}';
    }
}
