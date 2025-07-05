package com.example.project.service;

import com.example.project.model.User;
import com.example.project.dto.CreateUserDto;
import com.example.project.dto.UpdateUserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("/api/user")
    Call<List<User>> getUsers();

    @GET("/api/user/{id}")
    Call<User> getUserById(@Path("id") int id);

    @POST("/api/user")
    Call<User> createUser(@Body CreateUserDto user);

    @PUT("/api/user/{id}")
    Call<User> updateUser(@Path("id") int id, @Body UpdateUserDto user);

    @DELETE("/api/user/{id}")
    Call<Void> deleteUser(@Path("id") int id);
}
