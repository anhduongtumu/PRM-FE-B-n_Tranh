package com.example.project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.OrderHistoryAdapter;
import com.example.project.model.Order;
import com.example.project.service.OrderService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderHistoryActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://web-production-b71f7.up.railway.app/";
    private static final String TAG = "OrderHistoryActivity";

    private static final String PREF_NAME = "user_prefs";
    private static final String SESSION_PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private BottomNavigationView bottomNavigation;
    private RecyclerView recyclerOrderHistory;
    private OrderService orderService;
    private OrderHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Gắn nút back
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerOrderHistory = findViewById(R.id.recyclerOrderHistory);
        recyclerOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigation = findViewById(R.id.bottom_navigation);

        setupRetrofit();
        loadOrderHistory();
        setupBottomNavigation();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        orderService = retrofit.create(OrderService.class);
    }

    private int getUserIdFromPrefs() {
        SharedPreferences sessionPrefs = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sessionPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
        if (!isLoggedIn) return -1;

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1);
    }

    private void loadOrderHistory() {
        int userId = getUserIdFromPrefs();
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderService.getOrdersByUserId(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    adapter = new OrderHistoryAdapter(orders, OrderHistoryActivity.this);
                    recyclerOrderHistory.setAdapter(adapter);
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Failure: " + t.getMessage());
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_orders);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_orders) {
                return true;
            }
            return false;
        });
    }
}
