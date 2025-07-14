package com.example.project.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private int id; // Backend uses numeric ID
    private int userID; // Missing in your model
    private Date orderDate;
    private Double total; // Use Double to accept null
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus; // Rename to match backend
    private Date createdAt;
    private Date updatedAt;

    private List<OrderItem> OrderItems; // Assuming CartItem maps to OrderItem from backend

    // Constructors
    public Order() {
    }

    // Add constructor as needed

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public List<OrderItem> getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(List<OrderItem> OrderItems) {
        this.OrderItems = OrderItems;
    }
    // Helper methods
    public int getTotalItemCount() {
        int count = 0;
        if (OrderItems != null) {
            for (OrderItem item : OrderItems) {
                count += item.getQuantity();
            }
        }
        return count;
    }

    public boolean isDelivered() {
        return "Đã giao".equals(orderStatus);
    }

    public boolean isCancelled() {
        return "Đã hủy".equals(orderStatus);
    }

    public boolean isProcessing() {
        return "Đang xử lý".equals(orderStatus);
    }

    public boolean isShipping() {
        return "Đang giao".equals(orderStatus);
    }
}