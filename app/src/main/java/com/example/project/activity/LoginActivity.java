package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.project.R;
import com.example.project.dto.auth.LoginRequest;
import com.example.project.dto.auth.LoginResponse;
import com.example.project.utils.TokenManager;
import com.example.project.utils.UserManager;
import com.example.project.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToRegister;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.init(getApplicationContext());

        btnLogin.setOnClickListener(view -> loginUser());
        tvToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(email, password);

        loginViewModel.loginUser(loginRequest).observe(this, loginResponse -> {
            btnLogin.setEnabled(true);
            if (loginResponse != null) {
                // Save tokens securely
                TokenManager tokenManager = new TokenManager(getApplicationContext());
                tokenManager.saveToken(loginResponse.getAccessToken());
                //tokenManager.saveRefreshToken(loginResponse.getRefreshToken());

                // Save user info
                UserManager userManager = new UserManager(getApplicationContext());
                userManager.saveUser(loginResponse.getUser());

                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
