package com.example.project.service;

import com.example.project.model.Category;
import com.example.project.dto.category.CreateCategoryDto;
import com.example.project.dto.category.UpdateCategoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {
    @GET("/api/categories")
    Call<List<Category>> getAllCategories();

    @GET("/api/categories/{id}")
    Call<Category> getCategoryById(@Path("id") int id);

    @POST("/api/categories")
    Call<Category> createCategory(@Body CreateCategoryDto category);

    @PUT("/api/categories/{id}")
    Call<Category> updateCategory(@Path("id") int id, @Body UpdateCategoryDto category);

    @DELETE("/api/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);
}
