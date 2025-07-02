package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
// import com.example.project.adapter.WishlistAdapter;
// import com.example.project.manager.WishlistManager;
import com.example.project.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
// import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerWishlist;
    // private WishlistAdapter wishlistAdapter;
    private TextView tvWishlistCount;
    private TextView tvClearAll;
    private LinearLayout layoutEmptyWishlist;
    private MaterialButton btnStartShopping;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView btnBack;

    // Cart components
    private FrameLayout layoutCart;
    private FrameLayout layoutChat;
    private TextView tvCartBadge;
    private int cartItemCount = 0;

    // private List<Product> wishlistProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initViews();
        setupToolbar();
        setupDrawer();
        setupCartHandler();
        setupChatHandler();
        // setupWishlistRecyclerView();
        setupBottomNavigation();
        // loadWishlistData();

        // Show empty wishlist state by default
        showEmptyWishlistState();
    }

    private void initViews() {
        recyclerWishlist = findViewById(R.id.recyclerWishlist);
        tvWishlistCount = findViewById(R.id.tvWishlistCount);
        tvClearAll = findViewById(R.id.tvClearAll);
        layoutEmptyWishlist = findViewById(R.id.layoutEmptyWishlist);
        btnStartShopping = findViewById(R.id.btnStartShopping);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnBack = findViewById(R.id.btnBack);

        // Cart components
        layoutCart = findViewById(R.id.layoutCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);
        layoutChat = findViewById(R.id.layoutChat);
    }

    private void setupToolbar() {
        btnBack.setOnClickListener(v -> onBackPressed());

        // Clear all wishlist items - commented out functionality
        tvClearAll.setOnClickListener(v -> {
            // clearAllWishlist();
            // TODO: Implement clear all wishlist functionality
        });

        // Start shopping button
        btnStartShopping.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupDrawer() {
        // Setup navigation item selection
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (itemId == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (itemId == R.id.nav_map) {
            startActivity(new Intent(this, MapActivity.class));
        } else if (itemId == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (itemId == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private void setupCartHandler() {
        if (layoutCart != null) {
            layoutCart.setOnClickListener(v -> {
                Intent intent = new Intent(WishlistActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

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

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_wishlist);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_search) {
                    startActivity(new Intent(this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.nav_wishlist) {
                    return true; // Already on wishlist page
                } else if (itemId == R.id.nav_account) {
                    startActivity(new Intent(this, UserProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void setupChatHandler() {
        if (layoutChat != null) {
            layoutChat.setOnClickListener(v -> {
                Intent intent = new Intent(WishlistActivity.this, ChatActivity.class);
                startActivity(intent);
            });
        }
    }

    // COMMENTED OUT - Wishlist RecyclerView setup
    /*
    private void setupWishlistRecyclerView() {
        // Initialize with empty list
        wishlistAdapter = new WishlistAdapter(wishlistProducts);

        // Set up click listeners
        wishlistAdapter.setOnWishlistItemClickListener(new WishlistAdapter.OnWishlistItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Navigate to product detail
                Intent intent = new Intent(WishlistActivity.this, ProductDetailActivity.class);
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("originalPrice", product.getOriginalPrice());
                intent.putExtra("rating", product.getRating());
                intent.putExtra("category", product.getCategory());
                intent.putExtra("imageRes", product.getImageRes());
                startActivity(intent);
            }

            @Override
            public void onRemoveFromWishlist(Product product, int position) {
                removeFromWishlist(product, position);
            }

            @Override
            public void onAddToCart(Product product) {
                addToCart(product);
            }
        });

        // Set up RecyclerView with grid layout (2 columns)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerWishlist.setLayoutManager(layoutManager);
        recyclerWishlist.setAdapter(wishlistAdapter);
        recyclerWishlist.setHasFixedSize(true);
    }
    */

//    private void loadWishlistData() {
//        // Load wishlist data from WishlistManager or database
//        // For now, we'll use sample data
//        wishlistProducts = WishlistManager.getInstance().getWishlistProducts();
//
//        if (wishlistProducts.isEmpty()) {
//            // Add some sample wishlist items for demonstration
//            wishlistProducts = createSampleWishlistProducts();
//        }
//
//        wishlistAdapter.updateProducts(wishlistProducts);
//        updateWishlistUI();
//    }


    private List<Product> createSampleWishlistProducts() {
        List<Product> products = new ArrayList<>();

        // Corrected parameter order: (id, name, price, originalPrice, imageRes, rating, category, isOnSale)
        products.add(new Product(
                1,                               // int id
                "Tranh Trừu Tượng Hiện Đại",   // String name
                "899.000đ",                     // String price
                "1.200.000đ",                   // String originalPrice
                R.drawable.tranh1,              // int imageRes
                4.8f,                           // float rating
                "Trừu tượng",                   // String category
                true                            // boolean isOnSale
        ));

        products.add(new Product(
                2,                               // int id
                "Tranh Phong Cảnh Thiên Nhiên", // String name
                "750.000đ",                     // String price
                "950.000đ",                     // String originalPrice
                R.drawable.tranh2,              // int imageRes
                4.6f,                           // float rating
                "Phong cảnh",                   // String category
                true                            // boolean isOnSale
        ));

        products.add(new Product(
                3,                               // int id
                "Tranh Tối Giản Scandinavian",  // String name
                "650.000đ",                     // String price
                "800.000đ",                     // String originalPrice
                R.drawable.tranh3,              // int imageRes
                4.7f,                           // float rating
                "Tối giản",                     // String category
                true                            // boolean isOnSale
        ));

        return products;
    }

    private void showEmptyWishlistState() {
        // Hide wishlist content, show empty state
        recyclerWishlist.setVisibility(View.GONE);
        layoutEmptyWishlist.setVisibility(View.VISIBLE);
        tvClearAll.setVisibility(View.GONE);
        tvWishlistCount.setText("0 sản phẩm yêu thích");
    }

    // COMMENTED OUT - Update wishlist UI
    /*
    private void updateWishlistUI() {
        int itemCount = wishlistProducts.size();

        if (itemCount > 0) {
            // Show wishlist content
            recyclerWishlist.setVisibility(View.VISIBLE);
            layoutEmptyWishlist.setVisibility(View.GONE);
            tvClearAll.setVisibility(View.VISIBLE);

            // Update count text
            String countText = itemCount + " sản phẩm yêu thích";
            tvWishlistCount.setText(countText);
        } else {
            // Show empty state
            recyclerWishlist.setVisibility(View.GONE);
            layoutEmptyWishlist.setVisibility(View.VISIBLE);
            tvClearAll.setVisibility(View.GONE);
            tvWishlistCount.setText("0 sản phẩm yêu thích");
        }
    }
    */

    // COMMENTED OUT - Remove from wishlist
    /*
    private void removeFromWishlist(Product product, int position) {
        if (position >= 0 && position < wishlistProducts.size()) {
            wishlistProducts.remove(position);
            wishlistAdapter.notifyItemRemoved(position);
            wishlistAdapter.notifyItemRangeChanged(position, wishlistProducts.size());

            // Update wishlist manager
            WishlistManager.getInstance().removeProduct(product);

            updateWishlistUI();

            // Show snackbar with undo option
            Snackbar.make(recyclerWishlist,
                            product.getName() + " đã được xóa khỏi danh sách yêu thích",
                            Snackbar.LENGTH_LONG)
                    .setAction("Hoàn tác", v -> {
                        wishlistProducts.add(position, product);
                        wishlistAdapter.notifyItemInserted(position);
                        WishlistManager.getInstance().addProduct(product);
                        updateWishlistUI();
                    })
                    .show();
        }
    }
    */

    // COMMENTED OUT - Clear all wishlist
    /*
    private void clearAllWishlist() {
        if (!wishlistProducts.isEmpty()) {
            List<Product> tempProducts = new ArrayList<>(wishlistProducts);
            wishlistProducts.clear();
            wishlistAdapter.notifyDataSetChanged();
            WishlistManager.getInstance().clearAll();
            updateWishlistUI();

            // Show snackbar with undo option
            Snackbar.make(recyclerWishlist,
                            "Đã xóa tất cả sản phẩm yêu thích",
                            Snackbar.LENGTH_LONG)
                    .setAction("Hoàn tác", v -> {
                        wishlistProducts.addAll(tempProducts);
                        wishlistAdapter.notifyDataSetChanged();
                        for (Product product : tempProducts) {
                            WishlistManager.getInstance().addProduct(product);
                        }
                        updateWishlistUI();
                    })
                    .show();
        }
    }
    */

    // COMMENTED OUT - Add to cart
    /*
    private void addToCart(Product product) {
        cartItemCount++;
        updateCartBadge();

        // Show confirmation
        Snackbar.make(recyclerWishlist,
                        product.getName() + " đã được thêm vào giỏ hàng",
                        Snackbar.LENGTH_SHORT)
                .show();
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart count when returning to activity
        updateCartBadge();
        // loadWishlistData(); // Commented out
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
}