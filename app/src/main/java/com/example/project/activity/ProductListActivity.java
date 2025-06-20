package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.ProductAdapter;
import com.example.project.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProductList = findViewById(R.id.rvProductList);

        // Initialize RecyclerView
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Sample product list (replace with actual data source, e.g., API call)
        List<Product> products = createSampleProducts();

        // Set up RecyclerView
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        rvProductList.setAdapter(new ProductAdapter(products));
    }

    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                1,
                "Tranh Trừu Tượng Nghệ Thuật",
                "599.000đ",
                "799.000đ",
                R.drawable.tranh1, // You'll need to add these images
                4.8f,
                "Trừu tượng",
                true
        ));

        products.add(new Product(
                2,
                "Phong Cảnh Thiên Nhiên",
                "450.000đ",
                R.drawable.tranh1,
                4.6f,
                "Phong cảnh"
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
                R.drawable.tranh1,
                4.9f,
                "Hiện đại"
        ));

        products.add(new Product(
                5,
                "Tranh Tối Giản Đen Trắng",
                "280.000đ",
                R.drawable.tranh1,
                4.4f,
                "Tối giản"
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
                R.drawable.tranh1,
                4.7f,
                "Trừu tượng"
        ));

        products.add(new Product(
                8,
                "Rừng Xanh Mùa Thu",
                "420.000đ",
                R.drawable.tranh1,
                4.5f,
                "Phong cảnh"
        ));

        return products;
    }
}