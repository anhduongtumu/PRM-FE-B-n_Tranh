package com.example.project.service;

import com.example.project.model.Cart;
import com.example.project.dto.CreateCartDto;
import com.example.project.dto.UpdateCartDto;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartService {
    @GET("/api/carts")
    Call<List<Cart>> getAllCarts();

    @GET("/api/carts/{id}")
    Call<Cart> getCartById(@Path("id") int id);

    @GET("/api/carts/user/{userId}")
    Call<Cart> getCartByUserId(@Path("userId") int userId);

    @POST("api/carts/add-cart/{userId}")
    Call<Cart> addProductToCart(@Path("userId") int userId, @Body JsonObject body);

    @POST("/api/carts")
    Call<Cart> createCart(@Body CreateCartDto cart);

    @PUT("/api/carts/{id}")
    Call<Cart> updateCart(@Path("id") int id, @Body UpdateCartDto cart);

    @DELETE("/api/carts/{id}")
    Call<Void> deleteCart(@Path("id") int id);
}
