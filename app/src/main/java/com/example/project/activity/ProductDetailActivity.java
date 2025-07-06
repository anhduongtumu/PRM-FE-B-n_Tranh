package com.example.project.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String originalPrice = intent.getStringExtra("originalPrice");
        float rating = intent.getFloatExtra("rating", 0f);
        String imageUrl = intent.getStringExtra("imageUrl");
        String category = intent.getStringExtra("category");

        textName.setText(name != null ? name : "Không rõ");
        textPrice.setText(price != null ? price : "—");
        textRating.setText("Đánh giá: " + rating);
        textCategory.setText("Danh mục: " + (category != null ? category : "Không rõ"));

        if (originalPrice != null && !originalPrice.isEmpty()) {
            textOriginalPrice.setVisibility(View.VISIBLE);
            textOriginalPrice.setText(originalPrice);
            textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textOriginalPrice.setVisibility(View.GONE);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder_image).into(imageProduct);
        } else {
            imageProduct.setImageResource(R.drawable.placeholder_image);
        }

        btnAddToCart.setOnClickListener(v ->
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
        );
    }
}
