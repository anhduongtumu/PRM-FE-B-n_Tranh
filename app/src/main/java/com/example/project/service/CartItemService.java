package com.example.project.service;

import com.example.project.model.CartItem;
import com.example.project.dto.CreateCartItemDto;
import com.example.project.dto.UpdateCartItemDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartItemService {
    @GET("/api/cartitems")
    Call<List<CartItem>> getAllCartItems();

    @GET("/api/cartitems/{id}")
    Call<CartItem> getCartItemById(@Path("id") int id);

    @POST("/api/cartitems")
    Call<CartItem> createCartItem(@Body CreateCartItemDto cartItem);

    @PUT("/api/cartitems/{id}")
    Call<CartItem> updateCartItem(@Path("id") int id, @Body UpdateCartItemDto cartItem);

    @DELETE("/api/cartitems/{id}")
    Call<Void> deleteCartItem(@Path("id") int id);
}
