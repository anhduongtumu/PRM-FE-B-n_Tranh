
package com.example.project.model;

import java.io.Serializable;

public class Cart implements Serializable {
    private int id;
    private int userID;
    private double totalPrice;
    private String status;

    public Cart() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
