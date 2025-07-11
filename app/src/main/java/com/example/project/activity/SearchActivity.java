package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.ProductGridAdapter;
import com.example.project.model.Product;
import com.example.project.service.ProductService;
import com.example.project.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private EditText editTextSearch;
    private ImageView btnBack, btnClearSearch;
    private RecyclerView recyclerSearchResults;
    private TextView tvSearchResults;
    private LinearLayout layoutEmptyState, layoutNoResults;
    private BottomNavigationView bottomNavigation;

    // Filter buttons
    private MaterialButton btnFilterAll, btnFilterAbstract, btnFilterLandscape,
            btnFilterPortrait;
    private String currentFilter = "Tất cả";
    private Integer currentCategoryId = null;

    private ProductGridAdapter searchAdapter;
    private List<Product> allProducts;
    private List<Product> filteredProducts;

    // API service
    private ProductService productService;

    // Category mapping
    private static final int CATEGORY_ABSTRACT = 3;
    private static final int CATEGORY_LANDSCAPE = 1;
    private static final int CATEGORY_PORTRAIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initApiService();
        setupSearchFunctionality();
        setupFilterButtons();
        setupBottomNavigation();
        setupRecyclerView();
        loadAllProducts();
    }

    private void initViews() {
        editTextSearch = findViewById(R.id.editTextSearch);
        btnBack = findViewById(R.id.btnBack);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        recyclerSearchResults = findViewById(R.id.recyclerSearchResults);
        tvSearchResults = findViewById(R.id.tvSearchResults);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutNoResults = findViewById(R.id.layoutNoResults);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Filter buttons
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterAbstract = findViewById(R.id.btnFilterAbstract);
        btnFilterLandscape = findViewById(R.id.btnFilterLandscape);
        btnFilterPortrait = findViewById(R.id.btnFilterPortrait);
    }

    private void initApiService() {
        productService = ApiClient.getClient(this).create(ProductService.class);
    }

    private void setupSearchFunctionality() {
        btnBack.setOnClickListener(v -> finish());

        btnClearSearch.setOnClickListener(v -> {
            editTextSearch.setText("");
            showEmptyState();
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnClearSearch.setVisibility(View.VISIBLE);
                    performApiSearch(s.toString());
                } else {
                    btnClearSearch.setVisibility(View.GONE);
                    showEmptyState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> applyFilter("Tất cả", null));
        btnFilterAbstract.setOnClickListener(v -> applyFilter("Trừu tượng", CATEGORY_ABSTRACT));
        btnFilterLandscape.setOnClickListener(v -> applyFilter("Phong cảnh", CATEGORY_LANDSCAPE));
        btnFilterPortrait.setOnClickListener(v -> applyFilter("Chân dung", CATEGORY_PORTRAIT));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_search);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
                return true; // Already on search page
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            } else if (itemId == R.id.nav_account) {
                // Handle account click
                return true;
            }
            return false;
        });
    }

    private void loadAllProducts() {
        showLoadingState();

        Call<List<Product>> call = productService.getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                hideLoadingState();

                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    filteredProducts = new ArrayList<>(allProducts);
                    Log.d(TAG, "Loaded " + allProducts.size() + " products");
                } else {
                    Log.e(TAG, "Failed to load products: " + response.message());
                    showErrorState("Không thể tải danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                hideLoadingState();
                Log.e(TAG, "Network error: " + t.getMessage());
                showErrorState("Lỗi kết nối mạng");
            }
        });
    }

    private void setupRecyclerView() {
        searchAdapter = new ProductGridAdapter(new ArrayList<>());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerSearchResults.setLayoutManager(layoutManager);
        recyclerSearchResults.setAdapter(searchAdapter);
    }

    private void performApiSearch(String query) {
        showLoadingState();

        Call<List<Product>> call = productService.getAllProducts(query, currentCategoryId, "name_asc");
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                hideLoadingState();

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> searchResults = response.body();
                    updateSearchResults(searchResults);
                    Log.d(TAG, "Search results: " + searchResults.size() + " products found");
                } else {
                    Log.e(TAG, "Search failed: " + response.message());
                    showErrorState("Không thể tìm kiếm sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                hideLoadingState();
                Log.e(TAG, "Search network error: " + t.getMessage());
                showErrorState("Lỗi kết nối mạng");
            }
        });
    }

    private void applyFilter(String category, Integer categoryId) {
        currentFilter = category;
        currentCategoryId = categoryId;
        updateFilterButtons();

        // Re-perform search with current query if there is one
        String currentQuery = editTextSearch.getText().toString().trim();
        if (!currentQuery.isEmpty()) {
            performApiSearch(currentQuery);
        } else {
            // If no search query, load products with filter
            loadProductsWithFilter(categoryId);
        }
    }

    private void loadProductsWithFilter(Integer categoryId) {
        showLoadingState();

        Call<List<Product>> call = productService.getAllProducts(null, categoryId, "name_asc");
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                hideLoadingState();

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    if (products.isEmpty()) {
                        showNoResults();
                    } else {
                        showResults(products);
                    }
                } else {
                    Log.e(TAG, "Filter failed: " + response.message());
                    showErrorState("Không thể lọc sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                hideLoadingState();
                Log.e(TAG, "Filter network error: " + t.getMessage());
                showErrorState("Lỗi kết nối mạng");
            }
        });
    }

    private void updateFilterButtons() {
        // Reset all buttons
        resetFilterButton(btnFilterAll);
        resetFilterButton(btnFilterAbstract);
        resetFilterButton(btnFilterLandscape);
        resetFilterButton(btnFilterPortrait);

        // Highlight selected button
        MaterialButton selectedButton = getFilterButton(currentFilter);
        if (selectedButton != null) {
            selectedButton.setTextColor(getColor(R.color.primary_color));
            selectedButton.setStrokeColor(getColorStateList(R.color.primary_color));
        }
    }

    private void resetFilterButton(MaterialButton button) {
        button.setTextColor(getColor(R.color.text_secondary));
        button.setStrokeColor(getColorStateList(R.color.text_secondary));
    }

    private MaterialButton getFilterButton(String category) {
        switch (category) {
            case "Tất cả": return btnFilterAll;
            case "Trừu tượng": return btnFilterAbstract;
            case "Phong cảnh": return btnFilterLandscape;
            case "Chân dung": return btnFilterPortrait;
            default: return null;
        }
    }

    private void updateSearchResults(List<Product> results) {
        if (results.isEmpty()) {
            showNoResults();
        } else {
            showResults(results);
        }
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        layoutNoResults.setVisibility(View.GONE);
        recyclerSearchResults.setVisibility(View.GONE);
        tvSearchResults.setVisibility(View.GONE);
    }

    private void showNoResults() {
        layoutEmptyState.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.VISIBLE);
        recyclerSearchResults.setVisibility(View.GONE);
        tvSearchResults.setVisibility(View.GONE);
    }

    private void showResults(List<Product> results) {
        layoutEmptyState.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);
        recyclerSearchResults.setVisibility(View.VISIBLE);
        tvSearchResults.setVisibility(View.VISIBLE);

        String resultText = "Tìm thấy " + results.size() + " sản phẩm";
        tvSearchResults.setText(resultText);

        searchAdapter.updateProducts(results);
    }

    private void showLoadingState() {
        // You can add a loading indicator here
        // For now, we'll just hide other views
        layoutEmptyState.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);
        recyclerSearchResults.setVisibility(View.GONE);
        tvSearchResults.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        // Hide loading indicator if you have one
    }

    private void showErrorState(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        showEmptyState();
    }
}