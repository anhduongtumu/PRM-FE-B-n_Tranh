package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.OrderItemAdapter;
import com.example.project.model.CartItem;
import com.example.project.model.Order;
import com.example.project.model.Product;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class OrderConfirmationActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView tvOrderId;
    private TextView tvOrderDate;
    private TextView tvPaymentMethod;
    private TextView tvTotalAmount;
    private TextView tvDeliveryAddress;
    private TextView tvEstimatedDelivery;
    private RecyclerView recyclerOrderItems;
    private MaterialButton btnTrackOrder;
    private MaterialButton btnContinueShopping;

    // Data
    private Order order;
    private OrderItemAdapter orderItemAdapter;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        initViews();
        setupFormatters();
        loadOrderData();
        setupClickListeners();
        setupRecyclerView();
        displayOrderInformation();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);
        recyclerOrderItems = findViewById(R.id.recyclerOrderItems);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void setupFormatters() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
    }

    private void loadOrderData() {
        // Get order data from intent
        Intent intent = getIntent();
        if (intent != null) {
            // In a real app, you would pass the order data from previous activity
            double totalAmount = intent.getDoubleExtra("total", 0.0);
            ArrayList<CartItem> cartItems = (ArrayList<CartItem>) intent.getSerializableExtra("cartItems");
            String paymentMethod = intent.getStringExtra("paymentMethod");
            String deliveryAddress = intent.getStringExtra("deliveryAddress");

            if (cartItems != null) {
                order = createOrderFromCart(cartItems, totalAmount, paymentMethod, deliveryAddress);
            } else {
                // Create sample order for demonstration
                order = createSampleOrder();
            }
        } else {
            // Create sample order for demonstration
            order = createSampleOrder();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

//        btnTrackOrder.setOnClickListener(v -> {
//            // Navigate to order tracking activity
//            Intent intent = new Intent(this, OrderTrackingActivity.class);
//            intent.putExtra("orderId", order.getOrderId());
//            startActivity(intent);
//        });

        btnContinueShopping.setOnClickListener(v -> {
            // Navigate back to main activity and clear back stack
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        orderItemAdapter = new OrderItemAdapter(order.getOrderItems());
        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrderItems.setAdapter(orderItemAdapter);
    }

    private void displayOrderInformation() {
        // Display order information
        tvOrderId.setText("#" + order.getOrderId());
        tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvTotalAmount.setText(formatPrice(order.getTotalAmount()));
        tvDeliveryAddress.setText(order.getDeliveryAddress());
        tvEstimatedDelivery.setText(order.getEstimatedDelivery());
    }

    private Order createOrderFromCart(List<CartItem> cartItems, double totalAmount,
                                      String paymentMethod, String deliveryAddress) {
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "Thanh toán khi nhận hàng");
        order.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : getDefaultAddress());
        order.setEstimatedDelivery("5-7 ngày làm việc");
        order.setOrderItems(cartItems);
        order.setStatus("Đang xử lý");

        return order;
    }

    private Order createSampleOrder() {
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setOrderDate(new Date());
        order.setTotalAmount(1230000.0);
        order.setPaymentMethod("Thanh toán khi nhận hàng");
        order.setDeliveryAddress(getDefaultAddress());
        order.setEstimatedDelivery("5-7 ngày làm việc");
        order.setOrderItems(createSampleOrderItems());
        order.setStatus("Đang xử lý");

        return order;
    }

    private String generateOrderId() {
        // Generate order ID with current date and random number
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String datePart = sdf.format(new Date());
        int randomPart = new Random().nextInt(1000);
        return "TRH" + datePart + String.format("%03d", randomPart);
    }

    private String getDefaultAddress() {
        return "Nguyễn Văn A\n123 Nguyễn Thị Minh Khai, Quận 1\nTP. Hồ Chí Minh\n0901234567";
    }

    private List<CartItem> createSampleOrderItems() {
        List<CartItem> items = new ArrayList<>();

        // Create sample order items (same as cart items)
        items.add(new CartItem(
                new Product(1, "Tranh Trừu Tượng Nghệ Thuật", "599.000đ", "799.000đ",
                        R.drawable.tranh1, 4.8f, "Trừu tượng", true), 2));

        items.add(new CartItem(
                new Product(2, "Phong Cảnh Thiên Nhiên", "450.000đ", "",
                        R.drawable.tranh2, 4.6f, "Phong cảnh", false), 1));

        items.add(new CartItem(
                new Product(3, "Tranh Hiện Đại Minimalist", "350.000đ", "450.000đ",
                        R.drawable.tranh3, 4.7f, "Hiện đại", true), 1));

        return items;
    }

    private String formatPrice(double price) {
        return currencyFormat.format(price) + "đ";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navigate back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
