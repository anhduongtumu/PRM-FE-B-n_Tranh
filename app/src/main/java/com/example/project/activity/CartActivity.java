package com.example.project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.CartAdapter;
import com.example.project.network.ApiClient;
import com.example.project.dto.cartItem.UpdateCartItemDto;
import com.example.project.model.Cart;
import com.example.project.model.CartItem;
import com.example.project.service.CartItemService;
import com.example.project.service.CartService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private static final String USER_PREFS = "user_prefs";
    private static final String USER_ID_KEY = "id";

    private int userId;
    private Cart userCart;

    private CartService cartService;
    private CartItemService cartItemService;

    private ImageView btnBack;
    private TextView tvClearCart;
    private LinearLayout layoutEmptyCart, layoutCartContent;
    private RecyclerView recyclerCartItems;
    private TextInputEditText etPromoCode;
    private MaterialButton btnApplyPromo, btnCheckout;
    private TextView tvSubtotal, tvShipping, tvDiscount, tvTotal;
    private LinearLayout layoutDiscount;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();

    private double subtotal = 0.0, shipping = 0.0, discount = 0.0, total = 0.0;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userId = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getInt(USER_ID_KEY, -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initServices();
        setupRecyclerView();
        setupClickListeners();
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        fetchCart();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvClearCart = findViewById(R.id.tvClearCart);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartContent = findViewById(R.id.layoutCartContent);
        recyclerCartItems = findViewById(R.id.recyclerCartItems);
        etPromoCode = findViewById(R.id.etPromoCode);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotal = findViewById(R.id.tvTotal);
        layoutDiscount = findViewById(R.id.layoutDiscount);
    }

    private void initServices() {
        cartService = ApiClient.getClient(this).create(CartService.class);
        cartItemService = ApiClient.getClient(this).create(CartItemService.class);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartItems.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        tvClearCart.setOnClickListener(v -> clearCart());
        btnApplyPromo.setOnClickListener(v -> applyPromoCode());
        btnCheckout.setOnClickListener(v -> proceedToCheckout());
    }

    private void fetchCart() {
        cartService.getCartByUserId(userId).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userCart = response.body();
                    fetchCartItems();
                } else {
                    showEmptyCart();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCartItems() {
        cartItemService.getAllCartItems().enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> filteredItems = new ArrayList<>();

                    Log.d("DEBUG", "userCartID=" + userCart.getId());
                    Log.d("DEBUG", "Total cart items received: " + response.body().size());

                    for (CartItem item : response.body()) {
                        Log.d("DEBUG", "CartItem: cartID=" + item.getCartID() + ", product=" + item.getProduct());
                        Log.d("DEBUG", "CartID comparison: " + item.getCartID() + " == " + userCart.getId() + " = " + (item.getCartID() == userCart.getId()));

                        // Thêm kiểm tra null safety và debug chi tiết
                        if (item.getProduct() != null) {
                            Log.d("DEBUG", "Product is not null: " + item.getProduct().getProductName());

                            // Sử dụng equals() thay vì == để so sánh
                            if (item.getCartID() == userCart.getId()) {
                                filteredItems.add(item);
                                Log.d("DEBUG", "Added item to filtered list: " + item.getProduct().getProductName());
                            } else {
                                Log.d("DEBUG", "CartID mismatch: " + item.getCartID() + " != " + userCart.getId());
                            }
                        } else {
                            Log.d("DEBUG", "Product is null for cartItem ID: " + item.getId());
                        }
                    }

                    Log.d("DEBUG", "Filtered items count: " + filteredItems.size());

                    cartItems.clear();
                    cartItems.addAll(filteredItems);

                    cartAdapter.updateCartItems(cartItems);
                    updateUI();
                } else {
                    Log.e("DEBUG", "Response unsuccessful or body null");
                    showEmptyCart();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("DEBUG", "API call failed: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi khi tải sản phẩm giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        Log.d("DEBUG", "updateUI: cartItems size = " + cartItems.size());

        if (cartItems.isEmpty()) {
            Log.d("DEBUG", "Showing empty cart");
            showEmptyCart();
        } else {
            Log.d("DEBUG", "Showing cart content");
            layoutEmptyCart.setVisibility(View.GONE);
            layoutCartContent.setVisibility(View.VISIBLE);
            tvClearCart.setVisibility(View.VISIBLE);
            calculatePrices();
            updatePriceViews();

            // debug thêm
            Log.d("DEBUG", "Cart content shown. First item: " + cartItems.get(0).getProduct().getProductName());
        }
    }

    private void showEmptyCart() {
        layoutEmptyCart.setVisibility(View.VISIBLE);
        layoutCartContent.setVisibility(View.GONE);
        tvClearCart.setVisibility(View.GONE);
    }

    private void calculatePrices() {
        subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        shipping = subtotal >= 1000000 ? 0.0 : 0.0;
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
        for (CartItem item : new ArrayList<>(cartItems)) {
            cartItemService.deleteCartItem(item.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    cartItems.remove(item);
                    cartAdapter.notifyDataSetChanged();
                    updateUI();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Lỗi khi xóa sản phẩm khỏi giỏ", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                break;
            case "FREESHIP":
                discount = shipping;
                break;
            case "WELCOME50":
                discount = Math.min(50000, subtotal * 0.05);
                break;
            default:
                Toast.makeText(this, "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
        }
        calculatePrices();
        updatePriceViews();
        etPromoCode.setText("");
        Toast.makeText(this, "Áp dụng mã giảm giá thành công", Toast.LENGTH_SHORT).show();
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, BillingActivity.class);
        intent.putExtra("total", total);
        intent.putExtra("subtotal", subtotal);
        intent.putExtra("shipping", shipping);
        intent.putExtra("discount", discount);
        intent.putExtra("cartItems", new ArrayList<>(cartItems));
        startActivity(intent);
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (item.getProduct() == null || item.getProduct().getId() == 0) {
            Toast.makeText(CartActivity.this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        CartService cartService = ApiClient.getClient(CartActivity.this).create(CartService.class);

        JsonObject body = new JsonObject();
        body.addProperty("productId", item.getProduct().getId());
        body.addProperty("quantity", newQuantity);

        cartService.updateCartItem(userId, body).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    item.setQuantity(newQuantity); // cập nhật local
                    calculatePrices();
                    updatePriceViews();
                } else {
                    Toast.makeText(CartActivity.this, "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemRemoved(CartItem item) {
        cartItemService.deleteCartItem(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                cartItems.remove(item);
                cartAdapter.updateCartItems(cartItems);
                recyclerCartItems.post(() -> updateUI()); // Delay nhẹ để đảm bảo cập nhật UI sau khi data đã vào RecyclerView
                Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
