package com.example.project.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;

import com.example.project.R;
import com.example.project.dto.auth.LoginResponse;
import com.example.project.dto.order.BillingDTO;
import com.example.project.dto.order.CashResponseDto;
import com.example.project.dto.order.VNPayResponseDTO;
import com.example.project.model.CartItem;
import com.example.project.network.ApiClient;
import com.example.project.service.OrderService;
import com.example.project.utils.UserManager;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillingActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView tvOrderId;
    private TextView tvSubtotal;
    private TextView tvShipping;
    private TextView tvDiscount;
    private TextView tvTotal;
    private LinearLayout layoutDiscount;
    private MaterialButton btnConfirmPayment;
    private MaterialButton btnCancelPayment;
    private CardView cardPaymentMethod;
    private LinearLayout layoutCashOnDelivery;
    private LinearLayout layoutVnpayPayment;
    private TextView tvCashOnDelivery;
    private TextView tvVnpayPayment;

    // Data
    private double subtotal;
    private double shipping;
    private double discount;
    private double total;
    private List<CartItem> cartItems;
    private String paymentMethod = "vnpay"; // Default to VNPay
    private String deliveryAddress;
    private String orderId;
    private int currentUserId;
    private UserManager userManager;

    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        userManager = new UserManager(this);
        currentUserId = userManager.getUser().getId();

        initViews();
        setupCurrencyFormat();
        getDataFromIntent();
        setupClickListeners();
        generateOrderId();
        updateUI();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotal = findViewById(R.id.tvTotal);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnCancelPayment = findViewById(R.id.btnCancelPayment);
        cardPaymentMethod = findViewById(R.id.cardPaymentMethod);
        layoutCashOnDelivery = findViewById(R.id.layoutCashOnDelivery);
        layoutVnpayPayment = findViewById(R.id.layoutVnpayPayment);
        tvCashOnDelivery = findViewById(R.id.tvCashOnDelivery);
        tvVnpayPayment = findViewById(R.id.tvVnpayPayment);
    }

    private void setupCurrencyFormat() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        subtotal = intent.getDoubleExtra("subtotal", 0.0);
        shipping = intent.getDoubleExtra("shipping", 0.0);
        discount = intent.getDoubleExtra("discount", 0.0);
        total = intent.getDoubleExtra("total", 0.0);
        cartItems = (List<CartItem>) intent.getSerializableExtra("cartItems");
        deliveryAddress = intent.getStringExtra("deliveryAddress");

        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        layoutCashOnDelivery.setOnClickListener(v -> selectPaymentMethod("cod"));
        layoutVnpayPayment.setOnClickListener(v -> selectPaymentMethod("vnpay"));

        btnConfirmPayment.setOnClickListener(v -> {
            if (paymentMethod.equals("vnpay")) {
                // Redirect to VNPay payment gateway
                processVnpayPayment();
            } else {
                // Cash on delivery - proceed directly
                processCashOnDelivery();
            }
        });

        btnCancelPayment.setOnClickListener(v -> {
            Toast.makeText(this, "Đã hủy thanh toán", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void selectPaymentMethod(String method) {
        paymentMethod = method;

        // Update UI based on selected payment method
        if (method.equals("vnpay")) {
            // Reset backgrounds
            layoutVnpayPayment.setBackgroundResource(R.drawable.bg_selected_payment);
            layoutCashOnDelivery.setBackgroundResource(R.drawable.bg_unselected_payment);

            btnConfirmPayment.setText("Thanh Toán VNPay");
        } else {
            // Reset backgrounds
            layoutCashOnDelivery.setBackgroundResource(R.drawable.bg_selected_payment);
            layoutVnpayPayment.setBackgroundResource(R.drawable.bg_unselected_payment);

            btnConfirmPayment.setText("Đặt Hàng");
        }
    }

    private void generateOrderId() {
        // Generate a random order ID
        Random random = new Random();
        orderId = "TH" + String.format("%06d", random.nextInt(999999));
        tvOrderId.setText("Mã đơn hàng: " + orderId);
    }

    private void updateUI() {
        tvSubtotal.setText(formatPrice(subtotal));
        tvShipping.setText(shipping == 0 ? "Miễn phí" : formatPrice(shipping));
        tvTotal.setText(formatPrice(total));

        if (discount > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-" + formatPrice(discount));
        } else {
            layoutDiscount.setVisibility(View.GONE);
        }

        // Set initial payment method selection
        selectPaymentMethod("vnpay");
    }

    private String formatPrice(double price) {
        return currencyFormat.format(price) + "đ";
    }

    private void processVnpayPayment() {
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang xử lý...");
        int cartId = cartItems.get(0).getCartID();

        BillingDTO billingDTO = new BillingDTO(currentUserId, cartId, "Billing Address");

        OrderService orderService = ApiClient.getClient(this).create(OrderService.class);
        orderService.checkoutVNPay(billingDTO).enqueue(new Callback<VNPayResponseDTO>() {
            @Override
            public void onResponse(Call<VNPayResponseDTO> call, Response<VNPayResponseDTO> response) {
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setText("Thanh toán VNPay");

                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getPaymentUrl();

                    // Open using Chrome Custom Tabs for better user experience
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                    customTabsIntent.launchUrl(BillingActivity.this, Uri.parse(paymentUrl));

                    // If you prefer using Intent:
                    // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                    // startActivity(browserIntent);
                } else {
                    Toast.makeText(BillingActivity.this, "Thanh toán thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VNPayResponseDTO> call, Throwable t) {
                Toast.makeText(BillingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void proceedToOrderConfirmation(int orderId) {
        try {
            Intent intent = new Intent(this, OrderConfirmationActivity.class);

            // Pass orderId để OrderConfirmationActivity load từ API
            intent.putExtra("orderId", orderId);
            intent.putExtra("userId", currentUserId);

            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

            btnConfirmPayment.setEnabled(true);
            btnConfirmPayment.setText(paymentMethod.equals("vnpay") ? "Thanh Toán VNPay" : "Đặt Hàng");
        }
    }

    private void processCashOnDelivery() {
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang xử lý...");

        int cartId = cartItems.get(0).getCartID();  // Giả định tất cả sản phẩm trong 1 cart

        BillingDTO billingDTO = new BillingDTO(currentUserId, cartId, deliveryAddress);

        OrderService orderService = ApiClient.getClient(this).create(OrderService.class);
        orderService.checkoutCOD(billingDTO).enqueue(new Callback<CashResponseDto>() {
            @Override
            public void onResponse(Call<CashResponseDto> call, Response<CashResponseDto> response) {
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setText("Đặt Hàng");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int orderId = response.body().getOrderId();

                    Toast.makeText(BillingActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    proceedToOrderConfirmation(orderId);  // truyền orderId
                } else {
                    Toast.makeText(BillingActivity.this, "Không thể tạo đơn hàng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CashResponseDto> call, Throwable t) {
                Toast.makeText(BillingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setText("Đặt Hàng");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}