package com.example.project.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.project.R;
import com.example.project.adapter.BannerAdapter;
import com.example.project.adapter.ProductAdapter;
import com.example.project.dto.cart.CreateCartDto;
import com.example.project.dto.cartItem.CreateCartItemDto;
import com.example.project.model.Cart;
import com.example.project.model.CartItem;
import com.example.project.model.Product;
import com.example.project.network.ApiClient;
import com.example.project.service.AuthService;
import com.example.project.service.CartItemService;
import com.example.project.service.CartService;
import com.example.project.service.ProductService;
import com.example.project.utils.CartManager;
import com.example.project.utils.TokenManager;
import com.example.project.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerHotProducts;
    private ProductAdapter productAdapter;
    private ViewPager2 viewPagerBanner;
    private LinearLayout layoutIndicators;
    private TextView tvViewAll;
    private BannerAdapter bannerAdapter;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ImageView btnMenu;

    // Cart components
    private FrameLayout layoutCart;
    private FrameLayout layoutChat;
    private ImageView btnCart;
    private TextView tvCartBadge;
    private int cartItemCount = 0;

    // Cart management
    private SharedPreferences cartPrefs;
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_COUNT_KEY = "cart_count";

    private UserManager userManager;

    // Banner images array
    private int[] bannerImages = {
            R.drawable.tranh1,
            R.drawable.tranh2,
            R.drawable.tranh3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Handle deep link for payment status
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && "cuahangtranh".equals(data.getScheme())) {
            String status = data.getQueryParameter("status");
            String message = data.getQueryParameter("message");
            if ("fail".equals(status)) {
                // Show payment error
                showPaymentError(message);
            }
        }

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d("FCM", "Token: " + token);
                        // Save to Firestore under userId if logged in
                    }
                });

        userManager = new UserManager(this);

        initViews();
        initCartPreferences();
        setupDrawer();
        setupCartHandler();
        setupChatHandler();
        setupBanner();
        setupAutoSlide();
        setupProductRecyclerView();
        setupBottomNavigation();
        updateNavigationMenu();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        layoutIndicators = findViewById(R.id.layoutIndicators);
        recyclerHotProducts = findViewById(R.id.recyclerHotProducts);
        tvViewAll = findViewById(R.id.tvViewAll);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btnMenu);

        // Initialize cart components
        layoutCart = findViewById(R.id.layoutCart);
        btnCart = findViewById(R.id.btnCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);
        layoutChat = findViewById(R.id.layoutChat);

        // Navigation to ProductListActivity
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, ProductListActivity.class));
            });
        }
    }

    private void initCartPreferences() {
        cartPrefs = getSharedPreferences(CART_PREFS, MODE_PRIVATE);
    }

    private void setupCartHandler() {
        if (layoutCart != null) {
            layoutCart.setOnClickListener(v -> {
                // Navigate to CartActivity
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

        // Initialize cart badge
        updateCartBadge(this, tvCartBadge);
    }

    public static void updateCartBadge(Context context, TextView tvCartBadge) {
        UserManager userManager = new UserManager(context);
        int userId = userManager.getUser().getId();

        CartService cartService = ApiClient.getClient(context).create(CartService.class);
        Call<Cart> call = cartService.getCartByUserId(userId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cart cart = response.body();
                    List<CartItem> items = cart.getCartItems();

                    int totalQuantity = 0;
                    if (items != null) {
                        for (CartItem item : items) {
                            totalQuantity += item.getQuantity();
                        }
                    }

                    if (totalQuantity > 0) {
                        tvCartBadge.setVisibility(View.VISIBLE);
                        tvCartBadge.setText(String.valueOf(totalQuantity));
                    } else {
                        tvCartBadge.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CartManager", "Failed to get cart count", t);
            }
        });
    }

    private void setupChatHandler() {
        if (layoutChat != null) {
            layoutChat.setOnClickListener(v -> {
                SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

                if (!isLoggedIn) {
                    Toast.makeText(MainActivity.this, "Vui lòng tạo tài khoản trước", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }

                // Check if user is admin
                if (userManager.getUser().getRole().equals("admin")) {
                    Intent intent = new Intent(MainActivity.this, AdminChatsActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupDrawer() {
        // Setup hamburger menu click
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        // Setup navigation item selection
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            // Home
        } else if (itemId == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (itemId == R.id.nav_map) {
            startActivity(new Intent(this, MapActivity.class));
        } else if (itemId == R.id.nav_login) {
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

            if (isLoggedIn) {
                logoutUser();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        } else if (itemId == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true; // Already on home page
                } else if (itemId == R.id.nav_search) {
                    startActivity(new Intent(this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    // startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    startActivity(new Intent(this, UserProfileActivity.class));
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    startActivity(new Intent(this, OrderHistoryActivity.class));
                    return true;
                }

                return false;
            });
        }
    }

    private void setupBanner() {
        bannerAdapter = new BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(bannerAdapter);

        setupIndicators();
        setCurrentIndicator(0);

        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[bannerImages.length];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_inactive
                ));
            }
        }
    }

    private void setupAutoSlide() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerBanner.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerImages.length;
                viewPagerBanner.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // 3 seconds delay
            }
        };

        // Start auto-slide after 3 seconds
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        // Refresh cart count when returning to activity
        CartManager.updateCartBadge(this, tvCartBadge);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    private void setupProductRecyclerView() {
        // Gọi API để lấy danh sách sản phẩm từ server
        ProductService productService = ApiClient.getClient(this).create(ProductService.class);
        Call<List<Product>> call = productService.getAllProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body();

                    // Khởi tạo adapter và gán dữ liệu thực từ API
                    productAdapter = new ProductAdapter(productList);

                    productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
                        @Override
                        public void onProductClick(Product product) {
                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        }

                        @Override
                        public void onAddToCartClick(Product product) {
                            CartManager.addToCart(MainActivity.this, product, () -> {
                                CartManager.updateCartBadge(MainActivity.this, tvCartBadge);
                            });
                        }
                    });

                    LinearLayoutManager layoutManager = new LinearLayoutManager(
                            MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerHotProducts.setLayoutManager(layoutManager);
                    recyclerHotProducts.setAdapter(productAdapter);
                    recyclerHotProducts.setHasFixedSize(true);
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNavigationMenu() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        Menu menu = navigationView.getMenu();
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem registerItem = menu.findItem(R.id.nav_register);

        if (isLoggedIn) {
            loginItem.setTitle("Đăng xuất");
            registerItem.setVisible(false);
        } else {
            loginItem.setTitle("Đăng nhập");
            registerItem.setVisible(true);
        }
    }

    private void logoutUser() {
        AuthService authService = ApiClient.getClient(this).create(AuthService.class);
        Call<Void> call = authService.logout();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa token và trạng thái đăng nhập
                    TokenManager tokenManager = new TokenManager(MainActivity.this);
                    tokenManager.clearToken();
                    userManager.clearUser();

                    SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                    updateNavigationMenu();

                    // Optionally chuyển về LoginActivity hoặc Home
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể đăng xuất", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add this method to handle payment errors
    private void showPaymentError(String message) {
        String errorMessage = message != null ? message : "Thanh toán thất bại";
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

        // Optional: You can also show an AlertDialog for better user experience
        // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("Lỗi thanh toán")
        //        .setMessage(errorMessage)
        //        .setPositiveButton("OK", null)
        //        .show();
    }
}