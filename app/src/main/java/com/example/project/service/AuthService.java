package com.example.project.service;

import com.example.project.model.User;
import com.example.project.dto.CreateUserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/api/auth/register")
    Call<User> register(@Body CreateUserDto user);

    @POST("/api/auth/login")
    Call<User> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/refresh")
    Call<Void> refreshToken();

    @POST("/api/auth/logout")
    Call<Void> logout();

    @GET("/api/auth/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);

    class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
