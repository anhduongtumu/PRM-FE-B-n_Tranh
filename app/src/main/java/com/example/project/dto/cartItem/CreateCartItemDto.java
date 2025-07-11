package com.example.project.dto.cartItem;

public class CreateCartItemDto {
    private int cartID;
    private int productID;
    private int quantity;
    private double price;

    public CreateCartItemDto() {}

    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
