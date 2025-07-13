package com.example.project.dto.order;

public class VNPayResponseDTO {
    private String paymentUrl;
    private boolean success;

    public VNPayResponseDTO(String paymentUrl, boolean success) {
        this.paymentUrl = paymentUrl;
        this.success = success;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }



    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}

