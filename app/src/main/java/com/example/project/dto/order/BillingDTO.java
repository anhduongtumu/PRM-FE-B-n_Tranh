package com.example.project.dto.order;

public class BillingDTO {
    private int userId;
    private int cartId;
    private String billingAddress;

    public BillingDTO(int userId, int cartId, String billingAddress) {
        this.userId = userId;
        this.cartId = cartId;
        this.billingAddress = billingAddress;
    }
}
