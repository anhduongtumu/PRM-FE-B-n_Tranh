package com.example.project.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private TextView textFullDescription, textTechnicalSpecs;
    private Button btnAddToCart;

    // Quantity selection components
    private ImageButton btnDecreaseQuantity, btnIncreaseQuantity;
    private TextView textQuantity;
    private int currentQuantity = 1;
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        initializeViews();

        // Set back button listener
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Nhận đối tượng Product từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            populateProductDetails(product);
            setupQuantityControls();
        } else {
            Toast.makeText(this, "Không có dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        imageProduct = findViewById(R.id.imageProduct);
        textName = findViewById(R.id.textName);
        textPrice = findViewById(R.id.textPrice);
        textOriginalPrice = findViewById(R.id.textOriginalPrice);
        textRating = findViewById(R.id.textRating);
        textCategory = findViewById(R.id.textCategory);
        textFullDescription = findViewById(R.id.textFullDescription);
        textTechnicalSpecs = findViewById(R.id.textTechnicalSpecs);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Initialize quantity controls
        btnDecreaseQuantity = findViewById(R.id.btnDecreaseQuantity);
        btnIncreaseQuantity = findViewById(R.id.btnIncreaseQuantity);
        textQuantity = findViewById(R.id.textQuantity);
    }

    private void setupQuantityControls() {
        // Set initial quantity
        textQuantity.setText(String.valueOf(currentQuantity));

        // Decrease quantity button
        btnDecreaseQuantity.setOnClickListener(v -> {
            if (currentQuantity > MIN_QUANTITY) {
                currentQuantity--;
                updateQuantityDisplay();
            } else {
                Toast.makeText(this, "Số lượng tối thiểu là " + MIN_QUANTITY, Toast.LENGTH_SHORT).show();
            }
        });

        // Increase quantity button
        btnIncreaseQuantity.setOnClickListener(v -> {
            if (currentQuantity < MAX_QUANTITY) {
                currentQuantity++;
                updateQuantityDisplay();
            } else {
                Toast.makeText(this, "Số lượng tối đa là " + MAX_QUANTITY, Toast.LENGTH_SHORT).show();
            }
        });

        // Update button states
        updateQuantityDisplay();
    }

    private void updateQuantityDisplay() {
        textQuantity.setText(String.valueOf(currentQuantity));

        // Update button states
        btnDecreaseQuantity.setEnabled(currentQuantity > MIN_QUANTITY);
        btnIncreaseQuantity.setEnabled(currentQuantity < MAX_QUANTITY);

        // Optional: Change button appearance based on state
        btnDecreaseQuantity.setAlpha(currentQuantity > MIN_QUANTITY ? 1.0f : 0.5f);
        btnIncreaseQuantity.setAlpha(currentQuantity < MAX_QUANTITY ? 1.0f : 0.5f);
    }

    private void populateProductDetails(Product product) {
        // Set basic product information
        textName.setText(product.getProductName());
        textPrice.setText(String.format("%.0fđ", product.getPrice()));
        textRating.setText("Đánh giá: " + product.getRating());

        // Set category
        if (product.getCategory() != null && product.getCategory().getCategoryName() != null) {
            textCategory.setText("Danh mục: " + product.getCategory().getCategoryName());
        } else {
            textCategory.setText("Danh mục: Không rõ");
        }

        // Set original price with strikethrough if available
        if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
            textOriginalPrice.setVisibility(View.VISIBLE);
            textOriginalPrice.setText(product.getOriginalPrice());
            textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textOriginalPrice.setVisibility(View.GONE);
        }

        // Set full description
        if (product.getFullDescription() != null && !product.getFullDescription().isEmpty()) {
            textFullDescription.setText(product.getFullDescription());
        } else {
            textFullDescription.setText("Không có mô tả chi tiết.");
        }

        // Set technical specifications
        if (product.getTechnicalSpecifications() != null && !product.getTechnicalSpecifications().isEmpty()) {
            textTechnicalSpecs.setText(product.getTechnicalSpecifications());
        } else {
            textTechnicalSpecs.setText("Không có thông số kỹ thuật.");
        }

        // Load product image
        if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageProduct);
        } else {
            imageProduct.setImageResource(R.drawable.placeholder_image);
        }

        // Set add to cart button listener with quantity
        btnAddToCart.setOnClickListener(v -> {
            addProductToCart(product);
        });
    }

    private void addProductToCart(Product product) {
        // Show loading state
        btnAddToCart.setEnabled(false);
        btnAddToCart.setText("Đang thêm...");

        // Add product to cart with selected quantity
        CartManager.addToCart(ProductDetailActivity.this, product, currentQuantity, new CartManager.CartCallback() {
            @Override
            public void onSuccess() {
                // Reset button state
                btnAddToCart.setEnabled(true);
                btnAddToCart.setText("Thêm vào giỏ hàng");

                // Optional: Reset quantity to 1 after adding
                currentQuantity = 1;
                updateQuantityDisplay();
            }

            @Override
            public void onError(String error) {
                // Reset button state
                btnAddToCart.setEnabled(true);
                btnAddToCart.setText("Thêm vào giỏ hàng");

                // Error message is already shown by CartManager
            }
        });
    }
}