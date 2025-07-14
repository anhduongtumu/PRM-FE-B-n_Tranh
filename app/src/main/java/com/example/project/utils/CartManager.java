package com.example.project.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.model.Cart;
import com.example.project.model.CartItem;
import com.example.project.model.Product;
import com.example.project.network.ApiClient;
import com.example.project.service.CartItemService;
import com.example.project.service.CartService;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartManager {

    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_COUNT_KEY = "cart_count";

    // Callback interface for cart operations
    public interface CartCallback {
        void onSuccess();
        void onError(String error);
    }

    // Original method (for backward compatibility)
    public static void addToCart(Context context, Product product, Runnable onSuccess) {
        addToCart(context, product, 1, new CartCallback() {
            @Override
            public void onSuccess() {
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void onError(String error) {
                // Handle error silently for backward compatibility
            }
        });
    }

    // New method with quantity support and callback
    public static void addToCart(Context context, Product product, int quantity, CartCallback callback) {
        int userId = new UserManager(context).getUser().getId();
        Log.d("CartManager", "userId = " + userId + ", quantity = " + quantity);

        if (userId <= 0) {
            String errorMsg = "Không tìm thấy người dùng hợp lệ";
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onError(errorMsg);
            return;
        }

        if (quantity <= 0) {
            String errorMsg = "Số lượng phải lớn hơn 0";
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onError(errorMsg);
            return;
        }

        CartService cartService = ApiClient.getClient(context).create(CartService.class);

        JsonObject body = new JsonObject();
        body.addProperty("productId", product.getId());
        body.addProperty("quantity", quantity);

        cartService.addProductToCart(userId, body).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String successMsg = quantity == 1 ?
                            "Đã thêm vào giỏ hàng" :
                            "Đã thêm " + quantity + " sản phẩm vào giỏ hàng";
                    Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show();
                    updateCartCount(context, response.body().getId());
                    if (callback != null) callback.onSuccess();
                } else {
                    String errorMsg = "Không thể thêm sản phẩm";
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                String errorMsg = "Lỗi khi thêm sản phẩm: " + t.getMessage();
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onError(errorMsg);
            }
        });
    }

    // Method to update existing cart item quantity
    public static void updateCartItemQuantity(Context context, Product product, int newQuantity, CartCallback callback) {
        int userId = new UserManager(context).getUser().getId();
        Log.d("CartManager", "Updating cart item - userId = " + userId + ", newQuantity = " + newQuantity);

        if (userId <= 0) {
            String errorMsg = "Không tìm thấy người dùng hợp lệ";
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onError(errorMsg);
            return;
        }

        if (newQuantity <= 0) {
            String errorMsg = "Số lượng phải lớn hơn 0";
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onError(errorMsg);
            return;
        }

        CartService cartService = ApiClient.getClient(context).create(CartService.class);

        JsonObject body = new JsonObject();
        body.addProperty("productId", product.getId());
        body.addProperty("quantity", newQuantity);

        cartService.updateCartItem(userId, body).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                    updateCartCount(context, response.body().getId());
                    if (callback != null) callback.onSuccess();
                } else {
                    String errorMsg = "Không thể cập nhật số lượng";
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                String errorMsg = "Lỗi khi cập nhật: " + t.getMessage();
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onError(errorMsg);
            }
        });
    }

    private static void updateCartCount(Context context, int cartId) {
        CartItemService cartItemService = ApiClient.getClient(context).create(CartItemService.class);
        cartItemService.getAllCartItems().enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = 0;
                    for (CartItem item : response.body()) {
                        if (item.getCartID() == cartId) {
                            count += item.getQuantity(); // Count total quantity, not just items
                        }
                    }
                    SharedPreferences prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
                    prefs.edit().putInt(CART_COUNT_KEY, count).apply();
                    Log.d("CartManager", "Updated cart count: " + count);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("CartManager", "Failed to update cart count: " + t.getMessage());
            }
        });
    }

    // Method to get current cart count
    public static int getCartCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(CART_COUNT_KEY, 0);
    }

    // Method to refresh cart count from server
    public static void refreshCartCount(Context context, CartCallback callback) {
        int userId = new UserManager(context).getUser().getId();
        if (userId <= 0) {
            if (callback != null) callback.onError("Invalid user");
            return;
        }

        CartService cartService = ApiClient.getClient(context).create(CartService.class);
        cartService.getCartByUserId(userId).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateCartCount(context, response.body().getId());
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onError("Failed to refresh cart");
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                if (callback != null) callback.onError(t.getMessage());
            }
        });
    }

    public static void updateCartBadge(Context context, TextView tvCartBadge) {
        UserManager userManager = new UserManager(context);
        int userId = userManager.getUser().getId();

        CartService cartService = ApiClient.getClient(context).create(CartService.class);
        Call<Cart> call = cartService.getCartByUserId(userId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cart cart = response.body();
                    List<CartItem> items = cart.getCartItems();
                    int totalQuantity = 0;

                    if (items != null) {
                        for (CartItem item : items) {
                            totalQuantity += item.getQuantity();
                        }
                    }

                    if (totalQuantity > 0) {
                        tvCartBadge.setVisibility(View.VISIBLE);
                        tvCartBadge.setText(String.valueOf(totalQuantity));
                    } else {
                        tvCartBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CartManager", "Failed to get cart count", t);
            }
        });
    }

    public static void clearCartBadge(Context context, TextView badgeView) {
        SharedPreferences prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(CART_COUNT_KEY, 0).apply();
        badgeView.setVisibility(View.GONE);
    }
}