package com.example.project.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private Date orderDate;
    private double totalAmount;
    private String paymentMethod;
    private String deliveryAddress;
    private String estimatedDelivery;
    private List<CartItem> orderItems;
    private String status;
    private String trackingNumber;
    private Date deliveredDate;

    // Constructors
    public Order() {
    }

    public Order(String orderId, Date orderDate, double totalAmount, String paymentMethod,
                 String deliveryAddress, String estimatedDelivery, List<CartItem> orderItems, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.estimatedDelivery = estimatedDelivery;
        this.orderItems = orderItems;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public List<CartItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CartItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    // Helper methods
    public int getTotalItemCount() {
        int count = 0;
        if (orderItems != null) {
            for (CartItem item : orderItems) {
                count += item.getQuantity();
            }
        }
        return count;
    }

    public boolean isDelivered() {
        return "Đã giao".equals(status);
    }

    public boolean isCancelled() {
        return "Đã hủy".equals(status);
    }

    public boolean isProcessing() {
        return "Đang xử lý".equals(status);
    }

    public boolean isShipping() {
        return "Đang giao".equals(status);
    }
}