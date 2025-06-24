package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Paint;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textName, textPrice, textOriginalPrice, textRating, textCategory;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imageProduct = findViewById(R.id.imageProduct);
        textName = findViewById(R.id.textName);
        textPrice = findViewById(R.id.textPrice);
        textOriginalPrice = findViewById(R.id.textOriginalPrice);
        textRating = findViewById(R.id.textRating);
        textCategory = findViewById(R.id.textCategory);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Nhận dữ liệu từ intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String originalPrice = intent.getStringExtra("originalPrice");
        float rating = intent.getFloatExtra("rating", 0f);
        String category = intent.getStringExtra("category");
        int imageRes = intent.getIntExtra("imageRes", R.drawable.placeholder_image);

        // Gán dữ liệu
        textName.setText(name);
        textPrice.setText(price);
        textRating.setText("Đánh giá: " + rating);
        textCategory.setText("Danh mục: " + category);
        imageProduct.setImageResource(imageRes);

        if (originalPrice != null && !originalPrice.isEmpty()) {
            textOriginalPrice.setText(originalPrice);
            textOriginalPrice.setVisibility(View.VISIBLE);
            textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // TODO: xử lý thêm vào giỏ hàng nếu muốn
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }
}

