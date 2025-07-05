package com.example.project.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.project.dto.auth.LoginRequest;
import com.example.project.dto.auth.LoginResponse;
import com.example.project.repository.AuthRepository;

public class LoginViewModel extends ViewModel {
    private AuthRepository authRepository;

    // Initialize with context cleanly
    public void init(Context context) {
        if (authRepository == null) {
            authRepository = new AuthRepository(context);
        }
    }
    public LiveData<LoginResponse> loginUser(LoginRequest loginRequest) {
        return authRepository.loginUser(loginRequest);
    }
}
