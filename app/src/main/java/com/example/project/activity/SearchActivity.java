package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.ProductGridAdapter;
import com.example.project.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private ImageView btnBack, btnClearSearch;
    private RecyclerView recyclerSearchResults;
    private TextView tvSearchResults;
    private LinearLayout layoutEmptyState, layoutNoResults;
    private BottomNavigationView bottomNavigation;

    // Filter buttons
    private MaterialButton btnFilterAll, btnFilterAbstract, btnFilterLandscape,
            btnFilterModern, btnFilterMinimal;
    private String currentFilter = "Tất cả";

    private ProductGridAdapter searchAdapter;
    private List<Product> allProducts;
    private List<Product> filteredProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupSearchFunctionality();
        setupFilterButtons();
        setupBottomNavigation();
        loadAllProducts();
        setupRecyclerView();
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
        btnFilterModern = findViewById(R.id.btnFilterModern);
        btnFilterMinimal = findViewById(R.id.btnFilterMinimal);
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
                    performSearch(s.toString());
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
        btnFilterAll.setOnClickListener(v -> applyFilter("Tất cả"));
        btnFilterAbstract.setOnClickListener(v -> applyFilter("Trừu tượng"));
        btnFilterLandscape.setOnClickListener(v -> applyFilter("Phong cảnh"));
        btnFilterModern.setOnClickListener(v -> applyFilter("Hiện đại"));
        btnFilterMinimal.setOnClickListener(v -> applyFilter("Tối giản"));
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
        allProducts = createAllProducts();
        filteredProducts = new ArrayList<>(allProducts);
    }

    private void setupRecyclerView() {
        searchAdapter = new ProductGridAdapter(new ArrayList<>());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerSearchResults.setLayoutManager(layoutManager);
        recyclerSearchResults.setAdapter(searchAdapter);
    }

    private void performSearch(String query) {
        List<Product> searchResults = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase().trim();

        for (Product product : filteredProducts) {
            if (product.getName().toLowerCase().contains(lowercaseQuery) ||
                    product.getCategory().toLowerCase().contains(lowercaseQuery)) {
                searchResults.add(product);
            }
        }

        updateSearchResults(searchResults);
    }

    private void applyFilter(String category) {
        currentFilter = category;
        updateFilterButtons();

        if (category.equals("Tất cả")) {
            filteredProducts = new ArrayList<>(allProducts);
        } else {
            filteredProducts = new ArrayList<>();
            for (Product product : allProducts) {
                if (product.getCategory().equals(category)) {
                    filteredProducts.add(product);
                }
            }
        }

        // Re-perform search with current query if there is one
        String currentQuery = editTextSearch.getText().toString().trim();
        if (!currentQuery.isEmpty()) {
            performSearch(currentQuery);
        } else {
            showEmptyState();
        }
    }

    private void updateFilterButtons() {
        // Reset all buttons
        resetFilterButton(btnFilterAll);
        resetFilterButton(btnFilterAbstract);
        resetFilterButton(btnFilterLandscape);
        resetFilterButton(btnFilterModern);
        resetFilterButton(btnFilterMinimal);

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
            case "Hiện đại": return btnFilterModern;
            case "Tối giản": return btnFilterMinimal;
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

    private List<Product> createAllProducts() {
        List<Product> products = new ArrayList<>();

        // Trừu tượng
        products.add(new Product(1, "Tranh Trừu Tượng Nghệ Thuật", "599.000đ", "799.000đ",
                R.drawable.tranh1, 4.8f, "Trừu tượng", true));
        products.add(new Product(7, "Abstract Colorful Dreams", "680.000đ",
                R.drawable.tranh2, 4.7f, "Trừu tượng"));
        products.add(new Product(9, "Geometric Abstract Art", "420.000đ",
                R.drawable.tranh3, 4.6f, "Trừu tượng"));
        products.add(new Product(10, "Modern Abstract Waves", "550.000đ", "650.000đ",
                R.drawable.tranh1, 4.9f, "Trừu tượng", true));

        // Phong cảnh
        products.add(new Product(2, "Phong Cảnh Thiên Nhiên", "450.000đ",
                R.drawable.tranh2, 4.6f, "Phong cảnh"));
        products.add(new Product(6, "Cảnh Biển Hoàng Hôn", "520.000đ", "650.000đ",
                R.drawable.tranh3, 4.8f, "Phong cảnh", true));
        products.add(new Product(8, "Rừng Xanh Mùa Thu", "420.000đ",
                R.drawable.tranh1, 4.5f, "Phong cảnh"));
        products.add(new Product(11, "Mountain Landscape", "480.000đ",
                R.drawable.tranh2, 4.7f, "Phong cảnh"));

        // Hiện đại
        products.add(new Product(3, "Tranh Hiện Đại Minimalist", "350.000đ", "450.000đ",
                R.drawable.tranh3, 4.7f, "Hiện đại", true));
        products.add(new Product(4, "Nghệ Thuật Đương Đại", "720.000đ",
                R.drawable.tranh1, 4.9f, "Hiện đại"));
        products.add(new Product(12, "Contemporary Art Piece", "620.000đ",
                R.drawable.tranh2, 4.8f, "Hiện đại"));
        products.add(new Product(13, "Urban Modern Style", "380.000đ",
                R.drawable.tranh3, 4.4f, "Hiện đại"));

        // Tối giản
        products.add(new Product(5, "Tranh Tối Giản Đen Trắng", "280.000đ",
                R.drawable.tranh1, 4.4f, "Tối giản"));
        products.add(new Product(14, "Minimal Line Art", "320.000đ",
                R.drawable.tranh2, 4.6f, "Tối giản"));
        products.add(new Product(15, "Simple Geometric Design", "250.000đ",
                R.drawable.tranh3, 4.3f, "Tối giản"));
        products.add(new Product(16, "Clean Modern Art", "390.000đ", "450.000đ",
                R.drawable.tranh1, 4.7f, "Tối giản", true));

        return products;
    }
}
