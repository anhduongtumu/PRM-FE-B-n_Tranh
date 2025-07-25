package com.example.project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.OrderItemAdapter;
import com.example.project.model.CartItem;
import com.example.project.model.Order;
import com.example.project.model.Product;
import com.example.project.model.User;
import com.example.project.service.OrderService;
import com.example.project.service.UserService;
import com.example.project.utils.CartManager;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private User user;
    private OrderItemAdapter orderItemAdapter;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    private String paymentOrderId; // For deep link payment result

    // API Services
    private OrderService orderService;
    private UserService userService;
    private Retrofit retrofit;

    // Constants
    private static final String BASE_URL = "https://prm-be-ban-tranh.vercel.app/";
    private static final String TAG = "OrderConfirmation";

    // SharedPreferences constants (matching UserProfileActivity)
    private static final String PREF_NAME = "user_prefs";
    private static final String SESSION_PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        initViews();
        setupFormatters();
        setupRetrofit();
        handleDeepLink(); // Handle deep link first
        loadOrderData();
        CartManager.refreshCartCount(OrderConfirmationActivity.this );
        setupClickListeners();
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

    private void setupRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        orderService = retrofit.create(OrderService.class);
        userService = retrofit.create(UserService.class);
    }

    /**
     * Get user ID from SharedPreferences (matching UserProfileActivity structure)
     * @return userId if found, -1 if not found or not logged in
     */
    private int getUserIdFromSharedPreferences() {
        SharedPreferences sessionPrefs = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sessionPrefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (!isLoggedIn) {
            return -1; // User not logged in
        }

        SharedPreferences userPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return userPrefs.getInt(KEY_USER_ID, -1);
    }

    private void handleDeepLink() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null && "cuahangtranh".equals(data.getScheme())) {
            String host = data.getHost();
            String path = data.getPath();

            Log.d(TAG, "Deep link received: " + data.toString());

            if ("payment-result".equals(host)) {
                String status = data.getQueryParameter("status");
                paymentOrderId = data.getQueryParameter("orderId");

                if ("success".equalsIgnoreCase(status) && paymentOrderId != null) {
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Payment success, loading order: " + paymentOrderId);
                    loadOrderByPaymentId(paymentOrderId);
                } else if ("fail".equalsIgnoreCase(status)) {
                    String message = data.getQueryParameter("message");
                    Toast.makeText(this, "Thanh toán thất bại: " + (message != null ? message : ""), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Payment failed: " + message);
                } else {
                    Toast.makeText(this, "Trạng thái thanh toán không xác định", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Unknown payment status: " + status);
                }
            }
        }
    }

    private void loadOrderFromApi(int orderId) {
        Call<Order> call = orderService.getOrderById(orderId);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    order = response.body();
                    Log.d(TAG, "Order loaded successfully: " + order.getId());

                    setupRecyclerView();
                    displayOrderInformation();
                } else {
                    Log.e(TAG, "Failed to load order: " + response.code());
                    Toast.makeText(OrderConfirmationActivity.this,
                            "Không thể tải thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity instead of showing sample
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(OrderConfirmationActivity.this,
                        "Lỗi kết nối. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                finish(); // Close activity instead of showing sample
            }
        });
    }

    private void loadUserFromApi(int userId) {
        if (userId == -1) {
            Log.w(TAG, "Invalid user ID, cannot load user data");
            // Set default delivery address
            if (order != null && tvDeliveryAddress != null) {
                tvDeliveryAddress.setText("Chưa cung cấp địa chỉ giao hàng");
            }
            return;
        }

        Call<User> call = userService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    Log.d(TAG, "User loaded successfully: " + user.getUsername());

                    // Update delivery address with user information
                    updateDeliveryAddressWithUserInfo();
                } else {
                    Log.e(TAG, "Failed to load user: " + response.code());
                    // Set fallback delivery address
                    if (order != null && tvDeliveryAddress != null) {
                        tvDeliveryAddress.setText("Chưa cung cấp địa chỉ giao hàng");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "User API call failed: " + t.getMessage());
                // Set fallback delivery address
                if (order != null && tvDeliveryAddress != null) {
                    tvDeliveryAddress.setText("Lỗi tải thông tin giao hàng");
                }
            }
        });
    }

    private void updateDeliveryAddressWithUserInfo() {
        if (user != null) {
            // Format delivery address with user information from API
            StringBuilder addressBuilder = new StringBuilder();

            // Add username
            if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                addressBuilder.append("Tên: ").append(user.getUsername()).append("\n");
            }

            // Add phone number
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
                addressBuilder.append("SĐT: ").append(user.getPhoneNumber()).append("\n");
            }

            // Add address
            if (user.getAddress() != null && !user.getAddress().trim().isEmpty()) {
                addressBuilder.append("Địa chỉ: ").append(user.getAddress());
            } else {
                addressBuilder.append("Địa chỉ: Chưa cung cấp");
            }

            String formattedAddress = addressBuilder.toString();

            // Update the order object if it exists
            if (order != null) {
                order.setBillingAddress(user.getAddress());
            }

            // Update the display immediately
            runOnUiThread(() -> {
                if (tvDeliveryAddress != null) {
                    tvDeliveryAddress.setText(formattedAddress);
                }
            });
        } else {
            // No user data available
            runOnUiThread(() -> {
                if (tvDeliveryAddress != null) {
                    tvDeliveryAddress.setText("Chưa cung cấp địa chỉ giao hàng");
                }
            });
        }
    }

    private void loadOrderByPaymentId(String paymentOrderId) {
        // Try to parse the payment order ID to get the actual order ID
        try {
            int orderId = Integer.parseInt(paymentOrderId);
            loadOrderFromApi(orderId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Cannot parse payment order ID: " + paymentOrderId);
            Toast.makeText(this, "Không thể tải thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish(); // Close activity instead of showing sample
        }
    }

    private void loadOrderData() {
        Intent intent = getIntent();

        // Load current user from SharedPreferences
        int currentUserId = getUserIdFromSharedPreferences();

        if (currentUserId != -1) {
            loadUserFromApi(currentUserId);
        } else {
            Log.w(TAG, "User not logged in or user ID not found");
            if (tvDeliveryAddress != null) {
                tvDeliveryAddress.setText("Vui lòng đăng nhập để xem thông tin giao hàng");
            }
        }

        if (intent != null) {
            // Check if this is from payment deep link
            if (paymentOrderId != null) {
                return;
            } else {
                // Check if order ID is provided
                int orderId = intent.getIntExtra("orderId", -1);
                int userId = intent.getIntExtra("userId", -1);

                if (orderId != -1) {
                    loadOrderFromApi(orderId);
                    if (userId != -1 && userId != currentUserId) {
                        loadUserFromApi(userId);
                    }
                } else {
                    // Normal flow from cart/billing activity
                    int totalAmount = intent.getIntExtra("total", 0);
                    ArrayList<CartItem> cartItems = (ArrayList<CartItem>) intent.getSerializableExtra("cartItems");
                    String paymentMethod = intent.getStringExtra("paymentMethod");
                    String deliveryAddress = intent.getStringExtra("deliveryAddress");

                    // Add detailed logging
                    Log.d(TAG, "Loading order from cart data");
                    Log.d(TAG, "Total amount: " + totalAmount);
                    Log.d(TAG, "Payment method: " + paymentMethod);

                    if (cartItems != null) {
                        Log.d(TAG, "Cart items count: " + cartItems.size());
                        for (int i = 0; i < cartItems.size(); i++) {
                            CartItem item = cartItems.get(i);
                            Log.d(TAG, "Item " + i + ": " + item.getProduct().getProductName() +
                                    " (Qty: " + item.getQuantity() + ", Price: " + item.getPrice() + ")");
                        }

                        order = createOrderFromCart(cartItems, totalAmount, paymentMethod, deliveryAddress);
                        setupRecyclerView();
                        displayOrderInformation();

                        if (user != null) {
                            updateDeliveryAddressWithUserInfo();
                        }
                    } else {
                        Log.e(TAG, "Cart items is null!");
                        Toast.makeText(this, "Không có dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        } else {
            Log.e(TAG, "Intent is null!");
            Toast.makeText(this, "Không có dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnTrackOrder.setOnClickListener(v -> {
//            if (order != null) {
//                // Navigate to order tracking activity
//                Intent intent = new Intent(this, OrderTrackingActivity.class);
//                intent.putExtra("orderId", order.getOrderId());
//                startActivity(intent);
//            }
        });

        btnContinueShopping.setOnClickListener(v -> {
            // Navigate back to main activity and clear back stack
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        if (order != null && order.getOrderItems() != null) {
            Log.d(TAG, "Setting up RecyclerView with " + order.getOrderItems().size() + " items");

            // Create adapter
            orderItemAdapter = new OrderItemAdapter(order.getOrderItems());

            // Create LinearLayoutManager
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            // Setup RecyclerView with ENABLED scrolling
            recyclerOrderItems.setLayoutManager(layoutManager);
            recyclerOrderItems.setAdapter(orderItemAdapter);

            // ENABLE nested scrolling to work with ScrollView
            recyclerOrderItems.setNestedScrollingEnabled(true);

            // Set a fixed height or use match_parent for height
            ViewGroup.LayoutParams params = recyclerOrderItems.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            recyclerOrderItems.setLayoutParams(params);

            // Force RecyclerView to measure all items
            recyclerOrderItems.post(() -> {
                if (orderItemAdapter != null) {
                    orderItemAdapter.notifyDataSetChanged();
                }

                Log.d(TAG, "RecyclerView height: " + recyclerOrderItems.getHeight());
                Log.d(TAG, "RecyclerView child count: " + recyclerOrderItems.getChildCount());
                Log.d(TAG, "Adapter item count: " + orderItemAdapter.getItemCount());
            });

        } else {
            Log.e(TAG, "Order or orderItems is null");
        }
    }

    private void displayOrderInformation() {
        if (order != null) {
            // Display order information
            tvOrderId.setText("#" + order.getId());
            tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
            tvPaymentMethod.setText(order.getPaymentMethod());
            tvTotalAmount.setText(formatPrice(order.getTotal()));
            tvEstimatedDelivery.setText("5-7 ngày làm việc");

            // Only set delivery address if user info hasn't been loaded yet
            if (user == null && order.getBillingAddress() != null) {
                tvDeliveryAddress.setText(order.getBillingAddress());
            }
        }
    }

    private Order createOrderFromCart(List<CartItem> cartItems, double totalAmount,
                                      String paymentMethod, String deliveryAddress) {
        Order order = new Order();
        order.setId(order.getId());
        order.setOrderDate(new Date());
        order.setTotal(totalAmount);
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "Thanh toán khi nhận hàng");

        // Set delivery address - will be updated by updateDeliveryAddressWithUserInfo() if user data is available
        order.setBillingAddress(deliveryAddress != null ? deliveryAddress : "Đang tải thông tin giao hàng...");

//        order.setOrderItems(cartItems);
        order.setOrderStatus("Đang xử lý");

        return order;
    }

    private String generateOrderId() {
        // Generate order ID with current date and random number
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String datePart = sdf.format(new Date());
        int randomPart = new Random().nextInt(1000);
        return "TRH" + datePart + String.format("%03d", randomPart);
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