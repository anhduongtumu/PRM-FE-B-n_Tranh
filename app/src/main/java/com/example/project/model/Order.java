
package com.example.project.model;

import java.io.Serializable;

public class Order implements Serializable {
    private int id;
    private int cartID;
    private int userID;
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus;
    private String orderDate;

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}
