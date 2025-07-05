package com.example.project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    private double subtotal = 0.0;
    private double shipping = 30000.0;
    private double discount = 0.0;
    private double total = 0.0;

    private NumberFormat currencyFormat;

    private SharedPreferences cartPrefs;
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_COUNT_KEY = "cart_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setupCurrencyFormat();
        setupClickListeners();
        initCartPreferences();
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

    private void initCartPreferences() {
        cartPrefs = getSharedPreferences(CART_PREFS, MODE_PRIVATE);
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
        cartItems = createSampleCartItems();
        updateCartCount();
    }

    private void setupRecyclerViews() {
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
            double price = item.getProduct().getPrice();
            subtotal += price * item.getQuantity();
        }

        shipping = subtotal >= 1000000 ? 0.0 : 30000.0;
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

    private String formatPrice(double price) {
        return currencyFormat.format(price) + "đ";
    }

    private void clearCart() {
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateCartCount();
        updateUI();
        Toast.makeText(this, "Đã xóa tất cả sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    private void applyPromoCode() {
        String promoCode = etPromoCode.getText().toString().trim();
        if (promoCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (promoCode.toUpperCase()) {
            case "TRANH10":
                discount = subtotal * 0.1;
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
                discount = Math.min(50000, subtotal * 0.05);
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
            Intent intent = new Intent(this, BillingActivity.class);
            intent.putExtra("total", total);
            intent.putExtra("subtotal", subtotal);
            intent.putExtra("shipping", shipping);
            intent.putExtra("discount", discount);
            intent.putExtra("cartItems", (Serializable) new ArrayList<>(cartItems));
            intent.putExtra("deliveryAddress", "Nguyễn Văn A\n123 Nguyễn Thị Minh Khai, Quận 1\nTP. Hồ Chí Minh\n0901234567");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi xảy ra khi chuyển đến trang thanh toán. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateCartCount() {
        int totalItems = 0;
        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
        }
        cartPrefs.edit().putInt(CART_COUNT_KEY, totalItems).apply();
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            cartItems.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
        cartAdapter.notifyDataSetChanged();
        updateCartCount();
        updateUI();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        cartItems.remove(item);
        cartAdapter.notifyDataSetChanged();
        updateCartCount();
        updateUI();
        Toast.makeText(this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    private List<CartItem> createSampleCartItems() {
        List<CartItem> items = new ArrayList<>();
        Product p1 = new Product(1, "Tranh Trừu Tượng Nghệ Thuật", "Mô tả", "Chi tiết", "60x40cm", 599000, "https://example.com/tranh1.jpg", "Trừu tượng");
        Product p2 = new Product(2, "Phong Cảnh Thiên Nhiên", "Mô tả", "Chi tiết", "50x50cm", 450000, "https://example.com/tranh2.jpg", "Phong cảnh");
        items.add(new CartItem(p1, 2));
        items.add(new CartItem(p2, 1));
        return items;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void finish() {
        super.finish();
        updateCartCount();
    }
}
