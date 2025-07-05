package com.example.project.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project.dto.auth.LoginRequest;
import com.example.project.dto.auth.LoginResponse;
import com.example.project.network.ApiClient;
import com.example.project.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private AuthService authService;

    public AuthRepository(Context context) {
        authService = ApiClient.getClient(context).create(AuthService.class);
    }

    public LiveData<LoginResponse> loginUser(LoginRequest loginRequest) {
        MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();

        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResponseLiveData.postValue(response.body());
                } else {
                    loginResponseLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResponseLiveData.postValue(null);
            }
        });

        return loginResponseLiveData;
    }
}
