package com.example.project.service;

import com.example.project.model.Product;
import com.example.project.dto.product.CreateProductDto;
import com.example.project.dto.product.UpdateProductDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    @GET("/api/products")
    Call<List<Product>> getAllProducts();

    @GET("/api/products")
    Call<List<Product>> getAllProducts(
            @Query("search") String search,
            @Query("categoryId") Integer categoryId,
            @Query("sort") String sort
    );

    @GET("/api/products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @POST("/api/products")
    Call<Product> createProduct(@Body CreateProductDto product);

    @PUT("/api/products/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body UpdateProductDto product);

    @DELETE("/api/products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);
}