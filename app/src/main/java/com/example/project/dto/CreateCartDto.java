package com.example.project.dto;

public class CreateCartDto {
    private int userID;
    private double totalPrice;
    private String status;

    public CreateCartDto() {}

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
