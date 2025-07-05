package com.example.project.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.project.R;
import com.example.project.model.CartItem;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BillingActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView tvOrderId;
    private TextView tvSubtotal;
    private TextView tvShipping;
    private TextView tvDiscount;
    private TextView tvTotal;
    private LinearLayout layoutDiscount;
    private ImageView ivMomoQR;
    private TextView tvMomoInstructions;
    private TextView tvCountdown;
    private MaterialButton btnConfirmPayment;
    private MaterialButton btnCancelPayment;
    private CardView cardPaymentMethod;
    private LinearLayout layoutCashOnDelivery;
    private LinearLayout layoutMomoPayment;
    private TextView tvCashOnDelivery;
    private TextView tvMomoPayment;

    // Data
    private double subtotal;
    private double shipping;
    private double discount;
    private double total;
    private List<CartItem> cartItems;
    private String paymentMethod = "momo"; // Default to MoMo
    private String deliveryAddress;
    private String orderId;
    private NumberFormat currencyFormat;
    private CountDownTimer paymentTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        initViews();
        setupCurrencyFormat();
        getDataFromIntent();
        setupClickListeners();
        generateOrderId();
        updateUI();
        generateMomoQR();
        startPaymentTimer();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotal = findViewById(R.id.tvTotal);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        ivMomoQR = findViewById(R.id.ivMomoQR);
        tvMomoInstructions = findViewById(R.id.tvMomoInstructions);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnCancelPayment = findViewById(R.id.btnCancelPayment);
        cardPaymentMethod = findViewById(R.id.cardPaymentMethod);
        layoutCashOnDelivery = findViewById(R.id.layoutCashOnDelivery);
        layoutMomoPayment = findViewById(R.id.layoutMomoPayment);
        tvCashOnDelivery = findViewById(R.id.tvCashOnDelivery);
        tvMomoPayment = findViewById(R.id.tvMomoPayment);
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
        btnBack.setOnClickListener(v -> {
            if (paymentTimer != null) {
                paymentTimer.cancel();
            }
            finish();
        });

        layoutCashOnDelivery.setOnClickListener(v -> selectPaymentMethod("cod"));
        layoutMomoPayment.setOnClickListener(v -> selectPaymentMethod("momo"));

        btnConfirmPayment.setOnClickListener(v -> {
            if (paymentMethod.equals("momo")) {
                // Simulate payment verification
                simulatePaymentVerification();
            } else {
                proceedToOrderConfirmation();
            }
        });

        btnCancelPayment.setOnClickListener(v -> {
            if (paymentTimer != null) {
                paymentTimer.cancel();
            }
            Toast.makeText(this, "Đã hủy thanh toán", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void selectPaymentMethod(String method) {
        paymentMethod = method;

        // Update UI based on selected payment method
        if (method.equals("momo")) {
            tvMomoPayment.setBackgroundResource(R.drawable.bg_selected_payment);
            tvCashOnDelivery.setBackgroundResource(R.drawable.bg_unselected_payment);

            // Show MoMo QR section
            ivMomoQR.setVisibility(View.VISIBLE);
            tvMomoInstructions.setVisibility(View.VISIBLE);
            tvCountdown.setVisibility(View.VISIBLE);
            btnConfirmPayment.setText("Xác Nhận Thanh Toán");

            generateMomoQR();
            startPaymentTimer();
        } else {
            tvCashOnDelivery.setBackgroundResource(R.drawable.bg_selected_payment);
            tvMomoPayment.setBackgroundResource(R.drawable.bg_unselected_payment);

            // Hide MoMo QR section
            ivMomoQR.setVisibility(View.GONE);
            tvMomoInstructions.setVisibility(View.GONE);
            tvCountdown.setVisibility(View.GONE);
            btnConfirmPayment.setText("Đặt Hàng");

            if (paymentTimer != null) {
                paymentTimer.cancel();
            }
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
        selectPaymentMethod("momo");
    }

    private String formatPrice(double price) {
        return currencyFormat.format(price) + "đ";
    }

    private void generateMomoQR() {
        try {
            // MoMo QR format: 2|99|{phone}|{name}|{amount}|{message}|0|0
            String momoPhone = "0901234567"; // Example MoMo phone number
            String momoName = "Tranh Cua Hang";
            String amount = String.valueOf((long) total);
            String message = "Thanh toan don hang " + orderId;

            String qrContent = String.format("2|99|%s|%s|%s|%s|0|0",
                    momoPhone, momoName, amount, message);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ivMomoQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPaymentTimer() {
        if (paymentTimer != null) {
            paymentTimer.cancel();
        }

        // 10 minute timer for payment
        paymentTimer = new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvCountdown.setText(String.format("Thời gian còn lại: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Hết thời gian thanh toán");
                Toast.makeText(BillingActivity.this, "Hết thời gian thanh toán", Toast.LENGTH_LONG).show();
                finish();
            }
        };
        paymentTimer.start();
    }

    private void simulatePaymentVerification() {
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang xác nhận...");

        // Simulate payment verification delay
        btnConfirmPayment.postDelayed(() -> {
            // Simulate successful payment (90% success rate)
            Random random = new Random();
            if (random.nextInt(10) < 9) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                proceedToOrderConfirmation();
            } else {
                Toast.makeText(this, "Thanh toán thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setText("Xác Nhận Thanh Toán");
            }
        }, 2000);
    }

    private void proceedToOrderConfirmation() {
        if (paymentTimer != null) {
            paymentTimer.cancel();
        }

        try {
            Intent intent = new Intent(this, OrderConfirmationActivity.class);

            // Pass order data
            intent.putExtra("orderId", orderId);
            intent.putExtra("total", total);
            intent.putExtra("subtotal", subtotal);
            intent.putExtra("shipping", shipping);
            intent.putExtra("discount", discount);
            intent.putExtra("cartItems", (Serializable) new ArrayList<>(cartItems));
            intent.putExtra("paymentMethod", paymentMethod.equals("momo") ? "MoMo" : "Thanh toán khi nhận hàng");
            intent.putExtra("deliveryAddress", deliveryAddress);
            intent.putExtra("paymentStatus", "paid");

            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paymentTimer != null) {
            paymentTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (paymentTimer != null) {
            paymentTimer.cancel();
        }
        super.onBackPressed();
    }
}
