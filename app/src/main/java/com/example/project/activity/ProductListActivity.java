package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.ProductAdapter;
import com.example.project.model.Product;
import com.example.project.network.ApiClient;
import com.example.project.service.ProductService;
import com.example.project.utils.CartManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Xử lý nút quay lại
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        rvProductList = findViewById(R.id.rvProductList);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        fetchProductsFromApi();
    }

    private void fetchProductsFromApi() {
        ProductService productService = ApiClient.getClient(this).create(ProductService.class);

        productService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    ProductAdapter adapter = new ProductAdapter(products);
                    rvProductList.setAdapter(adapter);

                    adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
                        @Override
                        public void onProductClick(Product product) {
                            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        }

                        @Override
                        public void onAddToCartClick(Product product) {
                            CartManager.addToCart(ProductListActivity.this, product, () ->
                                    Toast.makeText(ProductListActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                } else {
                    Toast.makeText(ProductListActivity.this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
