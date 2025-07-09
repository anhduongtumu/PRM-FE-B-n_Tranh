package com.example.project.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project.utils.CartManager;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textName, textPrice, textOriginalPrice, textRating, textCategory;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        imageProduct = findViewById(R.id.imageProduct);
        textName = findViewById(R.id.textName);
        textPrice = findViewById(R.id.textPrice);
        textOriginalPrice = findViewById(R.id.textOriginalPrice);
        textRating = findViewById(R.id.textRating);
        textCategory = findViewById(R.id.textCategory);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Nhận đối tượng Product từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            textName.setText(product.getProductName());
            textPrice.setText(String.format("%.0fđ", product.getPrice()));
            textRating.setText("Đánh giá: " + product.getRating());

            if (product.getCategory() != null && product.getCategory().getCategoryName() != null) {
                textCategory.setText("Danh mục: " + product.getCategory().getCategoryName());
            } else {
                textCategory.setText("Danh mục: Không rõ");
            }

            if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
                textOriginalPrice.setVisibility(View.VISIBLE);
                textOriginalPrice.setText(product.getOriginalPrice());
                textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textOriginalPrice.setVisibility(View.GONE);
            }

            if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
                Glide.with(this)
                        .load(product.getImageURL())
                        .placeholder(R.drawable.placeholder_image)
                        .into(imageProduct);
            } else {
                imageProduct.setImageResource(R.drawable.placeholder_image);
            }

            btnAddToCart.setOnClickListener(v -> {
                CartManager.addToCart(ProductDetailActivity.this, product, null);
            });
        } else {
            Toast.makeText(this, "Không có dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
