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

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<Product> products = createSampleProducts();

        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        ProductAdapter adapter = new ProductAdapter(products);
        rvProductList.setAdapter(adapter);

        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("name", product.getProductName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("originalPrice", product.getOriginalPrice());
                intent.putExtra("rating", product.getRating());
                intent.putExtra("category", product.getCategory());
                intent.putExtra("imageUrl", product.getImageURL());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                // TODO: Thêm vào giỏ hàng
            }
        });
    }

    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                1,
                "Tranh Trừu Tượng Nghệ Thuật",
                "599000",
                "799000",
                "https://example.com/tranh1.jpg",
                4,
                "Trừu tượng",
                "true"
        ));

        products.add(new Product(
                2,
                "Phong Cảnh Thiên Nhiên",
                "450000",
                "0",
                "https://example.com/tranh2.jpg",
                4,
                "Phong cảnh",
                "false"
        ));

//        products.add(new Product(
//                3,
//                "Tranh Hiện Đại Minimalist",
//                350000,
//                450000,
//                "https://example.com/tranh3.jpg",
//                4.7f,
//                "Hiện đại",
//                true
//        ));
//
//        products.add(new Product(
//                4,
//                "Nghệ Thuật Đương Đại",
//                720000,
//                0,
//                "https://example.com/tranh4.jpg",
//                4.9f,
//                "Hiện đại",
//                false
//        ));
//
//        products.add(new Product(
//                5,
//                "Tranh Tối Giản Đen Trắng",
//                280000,
//                0,
//                "https://example.com/tranh5.jpg",
//                4.4f,
//                "Tối giản",
//                false
//        ));
//
//        products.add(new Product(
//                6,
//                "Cảnh Biển Hoàng Hôn",
//                520000,
//                650000,
//                "https://example.com/tranh6.jpg",
//                4.8f,
//                "Phong cảnh",
//                true
//        ));
//
//        products.add(new Product(
//                7,
//                "Abstract Colorful Dreams",
//                680000,
//                0,
//                "https://example.com/tranh7.jpg",
//                4.7f,
//                "Trừu tượng",
//                false
//        ));
//
//        products.add(new Product(
//                8,
//                "Rừng Xanh Mùa Thu",
//                420000,
//                0,
//                "https://example.com/tranh8.jpg",
//                4.5f,
//                "Phong cảnh",
//                false
//        ));

        return products;
    }
}
