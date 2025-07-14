package com.example.project.dto.order;

public class CashResponseDto {
    private int orderId;
    private boolean success;

    public CashResponseDto(int orderId, boolean success) {
        this.orderId = orderId;
        this.success = success;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
