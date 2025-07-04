package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.CartAdapter;
import com.example.project.model.CartItem;
import com.example.project.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    // Views
    private ImageView btnBack;
    private TextView tvClearCart;
    private LinearLayout layoutEmptyCart;
    private LinearLayout layoutCartContent;
    private RecyclerView recyclerCartItems;
    private TextInputEditText etPromoCode;
    private MaterialButton btnApplyPromo;
    private TextView tvSubtotal;
    private TextView tvShipping;
    private TextView tvDiscount;
    private TextView tvTotal;
    private LinearLayout layoutDiscount;
    private MaterialButton btnCheckout;

    // Data
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    // Price calculations
    private double subtotal = 0.0;
    private double shipping = 30000.0; // 30,000 VND
    private double discount = 0.0;
    private double total = 0.0;

    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setupCurrencyFormat();
        setupClickListeners();
        loadCartData();
        setupRecyclerViews();
        updateUI();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvClearCart = findViewById(R.id.tvClearCart);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartContent = findViewById(R.id.layoutCartContent);
        recyclerCartItems = findViewById(R.id.recyclerCartItems);
        etPromoCode = findViewById(R.id.etPromoCode);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotal = findViewById(R.id.tvTotal);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void setupCurrencyFormat() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvClearCart.setOnClickListener(v -> clearCart());

        btnApplyPromo.setOnClickListener(v -> applyPromoCode());

        btnCheckout.setOnClickListener(v -> proceedToCheckout());
    }

    private void loadCartData() {
        // Load cart items (in a real app, this would come from database/SharedPreferences)
        cartItems = createSampleCartItems();
    }

    private void setupRecyclerViews() {
        // Cart Items RecyclerView
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartItems.setAdapter(cartAdapter);
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutCartContent.setVisibility(View.GONE);
            tvClearCart.setVisibility(View.GONE);
        } else {
            layoutEmptyCart.setVisibility(View.GONE);
            layoutCartContent.setVisibility(View.VISIBLE);
            tvClearCart.setVisibility(View.VISIBLE);
            calculatePrices();
            updatePriceViews();
        }
    }

    private void calculatePrices() {
        subtotal = 0.0;
        for (CartItem item : cartItems) {
            double price = parsePrice(item.getProduct().getPrice());
            subtotal += price * item.getQuantity();
        }

        // Free shipping for orders over 1,000,000 VND
        if (subtotal >= 1000000) {
            shipping = 0.0;
        } else {
            shipping = 30000.0;
        }

        total = subtotal + shipping - discount;
    }

    private void updatePriceViews() {
        tvSubtotal.setText(formatPrice(subtotal));
        tvShipping.setText(shipping == 0 ? "Miễn phí" : formatPrice(shipping));
        tvTotal.setText(formatPrice(total));

        if (discount > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-" + formatPrice(discount));
        } else {
            layoutDiscount.setVisibility(View.GONE);
        }
    }

    private double parsePrice(String priceString) {
        // Remove "đ" and "." from price string and convert to double
        return Double.parseDouble(priceString.replaceAll("[đ.,]", ""));
    }

    private String formatPrice(double price) {
        return currencyFormat.format(price) + "đ";
    }

    private void clearCart() {
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateUI();
        Toast.makeText(this, "Đã xóa tất cả sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    private void applyPromoCode() {
        String promoCode = etPromoCode.getText().toString().trim();

        if (promoCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sample promo codes
        switch (promoCode.toUpperCase()) {
            case "TRANH10":
                discount = subtotal * 0.1; // 10% discount
                Toast.makeText(this, "Áp dụng mã giảm giá thành công! Giảm 10%", Toast.LENGTH_SHORT).show();
                break;
            case "FREESHIP":
                if (shipping > 0) {
                    discount = shipping;
                    Toast.makeText(this, "Miễn phí vận chuyển!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Đơn hàng đã được miễn phí vận chuyển", Toast.LENGTH_SHORT).show();
                }
                break;
            case "WELCOME50":
                discount = Math.min(50000, subtotal * 0.05); // 50k max or 5%
                Toast.makeText(this, "Chào mừng! Giảm " + formatPrice(discount), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
        }

        calculatePrices();
        updatePriceViews();
        etPromoCode.setText("");
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create intent to navigate to OrderConfirmationActivity
            Intent intent = new Intent(this, OrderConfirmationActivity.class);

            // Pass order data to OrderConfirmationActivity
            intent.putExtra("total", total);
            intent.putExtra("subtotal", subtotal);
            intent.putExtra("shipping", shipping);
            intent.putExtra("discount", discount);
            intent.putExtra("cartItems", (Serializable) new ArrayList<>(cartItems));
            intent.putExtra("paymentMethod", "Thanh toán khi nhận hàng");
            intent.putExtra("deliveryAddress", "Nguyễn Văn A\n123 Nguyễn Thị Minh Khai, Quận 1\nTP. Hồ Chí Minh\n0901234567");

            // Start OrderConfirmationActivity
            startActivity(intent);

            // Clear cart after successful checkout
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();

            // Show success message
            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

            // Finish current activity
            finish();

        } catch (Exception e) {
            // Handle any errors during checkout
            Toast.makeText(this, "Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addToCart(Product product) {
        // Check if product already exists in cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                cartAdapter.notifyDataSetChanged();
                updateUI();
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add new item to cart
        cartItems.add(new CartItem(product, 1));
        cartAdapter.notifyDataSetChanged();
        updateUI();
        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // CartAdapter.OnCartItemListener implementation
    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            cartItems.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
        cartAdapter.notifyDataSetChanged();
        updateUI();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        cartItems.remove(item);
        cartAdapter.notifyDataSetChanged();
        updateUI();
        Toast.makeText(this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    private List<CartItem> createSampleCartItems() {
        List<CartItem> items = new ArrayList<>();

        // Sample cart items
        items.add(new CartItem(
                new Product(1, "Tranh Trừu Tượng Nghệ Thuật", "599.000đ", "799.000đ",
                        R.drawable.tranh1, 4.8f, "Trừu tượng", true), 2));

        items.add(new CartItem(
                new Product(2, "Phong Cảnh Thiên Nhiên", "450.000đ", "",
                        R.drawable.tranh2, 4.6f, "Phong cảnh", false), 1));

        items.add(new CartItem(
                new Product(3, "Tranh Hiện Đại Minimalist", "350.000đ", "450.000đ",
                        R.drawable.tranh3, 4.7f, "Hiện đại", true), 1));

        return items;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        updateUI();
    }
}