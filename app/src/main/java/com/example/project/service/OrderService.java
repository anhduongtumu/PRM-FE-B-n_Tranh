package com.example.project.service;

import com.example.project.model.Order;
import com.example.project.dto.CreateOrderDto;
import com.example.project.dto.UpdateOrderDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderService {
    @GET("/api/orders")
    Call<List<Order>> getAllOrders();

    @GET("/api/orders/{id}")
    Call<Order> getOrderById(@Path("id") int id);

    @GET("/api/orders/user/{userId}")
    Call<List<Order>> getOrdersByUserId(@Path("userId") int userId);

    @POST("/api/orders")
    Call<Order> createOrder(@Body CreateOrderDto order);

    @PUT("/api/orders/{id}")
    Call<Order> updateOrder(@Path("id") int id, @Body UpdateOrderDto order);

    @DELETE("/api/orders/{id}")
    Call<Void> deleteOrder(@Path("id") int id);
}
