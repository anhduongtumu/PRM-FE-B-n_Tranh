package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.example.project.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

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

        initViews();
        setupDrawer();
        setupCartHandler();
        setupChatHandler();
        setupBanner();
        setupAutoSlide();
        setupProductRecyclerView();
        setupBottomNavigation();
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

    private void setupCartHandler() {
        if (layoutCart != null) {
            layoutCart.setOnClickListener(v -> {
                // Navigate to CartActivity
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

        // Initialize cart badge
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (tvCartBadge != null) {
            if (cartItemCount > 0) {
                tvCartBadge.setText(String.valueOf(cartItemCount));
                tvCartBadge.setVisibility(View.VISIBLE);
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private void addToCart(Product product) {
        // Increment cart count
        cartItemCount++;

        // Update badge
        updateCartBadge();

        // You can also save to SharedPreferences or database here
        // For example:
        // CartManager.getInstance().addProduct(product);

        // Show a toast or snackbar to confirm addition
        // Toast.makeText(this, product.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    private void setupChatHandler() {
        if (layoutChat != null) {
            layoutChat.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
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
            // Already on home page
        } else if (itemId == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }else if (itemId == R.id.nav_map) {
            // Navigate to Map Activity
            Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(mapIntent);
        } else if (itemId == R.id.nav_wishlist) {
            startActivity(new Intent(this, WishlistActivity.class));
        } else if (itemId == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
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
                    // Handle notifications click
                    // startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    // Handle account click
                    startActivity(new Intent(this, UserProfileActivity.class));
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
        // cartItemCount = CartManager.getInstance().getItemCount();
        // updateCartBadge();
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
        // Tạo dữ liệu sản phẩm mẫu
        List<Product> sampleProducts = createSampleProducts();

        // Khởi tạo adapter và gán dữ liệu
        productAdapter = new ProductAdapter(sampleProducts);

        // Gán listener NGAY SAU KHI KHỞI TẠO adapter
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("originalPrice", product.getOriginalPrice());
                intent.putExtra("rating", product.getRating());
                intent.putExtra("category", product.getCategory());
                intent.putExtra("imageRes", product.getImageRes());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                // Handle add to cart
                addToCart(product);
            }
        });

        // Gán layout và adapter cho RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        recyclerHotProducts.setLayoutManager(layoutManager);
        recyclerHotProducts.setAdapter(productAdapter);
        recyclerHotProducts.setHasFixedSize(true);
    }

    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                1,
                "Tranh Trừu Tượng Nghệ Thuật",
                "599.000đ",
                "799.000đ",
                R.drawable.tranh1,
                4.8f,
                "Trừu tượng",
                true
        ));

        products.add(new Product(
                2,
                "Phong Cảnh Thiên Nhiên",
                "450.000đ",
                "",
                R.drawable.tranh1,
                4.6f,
                "Phong cảnh",
                false
        ));

        products.add(new Product(
                3,
                "Tranh Hiện Đại Minimalist",
                "350.000đ",
                "450.000đ",
                R.drawable.tranh1,
                4.7f,
                "Hiện đại",
                true
        ));

        products.add(new Product(
                4,
                "Nghệ Thuật Đương Đại",
                "720.000đ",
                "",
                R.drawable.tranh1,
                4.9f,
                "Hiện đại",
                false
        ));

        products.add(new Product(
                5,
                "Tranh Tối Giản Đen Trắng",
                "280.000đ",
                "",
                R.drawable.tranh1,
                4.4f,
                "Tối giản",
                false
        ));

        products.add(new Product(
                6,
                "Cảnh Biển Hoàng Hôn",
                "520.000đ",
                "650.000đ",
                R.drawable.tranh1,
                4.8f,
                "Phong cảnh",
                true
        ));

        products.add(new Product(
                7,
                "Abstract Colorful Dreams",
                "680.000đ",
                "",
                R.drawable.tranh1,
                4.7f,
                "Trừu tượng",
                false
        ));

        products.add(new Product(
                8,
                "Rừng Xanh Mùa Thu",
                "420.000đ",
                "",
                R.drawable.tranh1,
                4.5f,
                "Phong cảnh",
                false
        ));

        return products;
    }
}