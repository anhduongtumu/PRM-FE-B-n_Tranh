package com.example.project.model;
import java.io.Serializable;
import java.util.Date;

public class OrderItem implements Serializable {
    private int id;
    private int orderID;
    private int productID;
    private int quantity;
    private double price;
    private Date createdAt;
    private Date updatedAt;
    private Product Product;

    public OrderItem() {
    }

    public OrderItem(int id, int orderID, int productID, int quantity, double price,
                     Date createdAt, Date updatedAt, Product product) {
        this.id = id;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.Product = product;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Product getProduct() {
        return Product;
    }

    public void setProduct(Product product) {
        this.Product = product;
    }
}

