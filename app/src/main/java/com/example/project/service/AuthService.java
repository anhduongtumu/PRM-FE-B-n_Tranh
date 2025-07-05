package com.example.project.service;

import com.example.project.model.User;
import com.example.project.dto.CreateUserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import com.example.project.dto.auth.LoginRequest;
import com.example.project.dto.auth.LoginResponse;

public interface AuthService {
    @POST("/api/auth/register")
    Call<User> register(@Body CreateUserDto user);

    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/refresh")
    Call<Void> refreshToken();

    @POST("/api/auth/logout")
    Call<Void> logout();

    @GET("/api/auth/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);
}
