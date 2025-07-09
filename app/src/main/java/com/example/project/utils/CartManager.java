package com.example.project.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.dto.CreateCartDto;
import com.example.project.dto.CreateCartItemDto;
import com.example.project.model.Cart;
import com.example.project.model.CartItem;
import com.example.project.model.Product;
import com.example.project.network.ApiClient;
import com.example.project.service.CartItemService;
import com.example.project.service.CartService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartManager {

    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_COUNT_KEY = "cart_count";

    public static void addToCart(Context context, Product product, Runnable onSuccess) {
        int userId = new UserManager(context).getUser().getId();
        Log.d("CartManager", "userId = " + userId); // Thêm dòng này

        if (userId <= 0) {
            Toast.makeText(context, "Không tìm thấy người dùng hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        CartService cartService = ApiClient.getClient(context).create(CartService.class);

        cartService.getCartByUserId(userId).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int cartId = response.body().getId();
                    addCartItem(context, cartId, product, onSuccess);
                } else {
                    // Nếu chưa có thì tạo giỏ hàng mới
                    CreateCartDto createCartDto = new CreateCartDto();
                    createCartDto.setUserID(userId);

                    cartService.createCart(createCartDto).enqueue(new Callback<Cart>() {
                        @Override
                        public void onResponse(Call<Cart> call, Response<Cart> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                int newCartId = response.body().getId();
                                addCartItem(context, newCartId, product, onSuccess);
                            } else {
                                Toast.makeText(context, "Không thể tạo giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Cart> call, Throwable t) {
                            Toast.makeText(context, "Lỗi tạo giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(context, "Lỗi lấy giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void addCartItem(Context context, int cartId, Product product, Runnable onSuccess) {
        CreateCartItemDto dto = new CreateCartItemDto();
        dto.setCartID(cartId);
        dto.setProductID(product.getId());
        dto.setQuantity(1);
        dto.setPrice(product.getPrice());

        CartItemService cartItemService = ApiClient.getClient(context).create(CartItemService.class);
        cartItemService.createCartItem(dto).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    updateCartCount(context, cartId);
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Toast.makeText(context, "Không thể thêm sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                Toast.makeText(context, "Lỗi khi thêm sản phẩm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                            count++;
                        }
                    }
                    SharedPreferences prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
                    prefs.edit().putInt(CART_COUNT_KEY, count).apply();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                // Có thể log nếu cần
            }
        });
    }

    public static void updateCartBadge(Context context, TextView badgeView) {
        SharedPreferences prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        int count = prefs.getInt(CART_COUNT_KEY, 0);

        if (count > 0) {
            badgeView.setVisibility(View.VISIBLE);
            badgeView.setText(String.valueOf(count));
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    public static void clearCartBadge(Context context, TextView badgeView) {
        SharedPreferences prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(CART_COUNT_KEY, 0).apply();
        badgeView.setVisibility(View.GONE);
    }

}
