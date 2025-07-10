package com.example.project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project.R;
import com.example.project.model.User;
import com.example.project.service.UserService;
import com.example.project.dto.user.UpdateUserDto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etAddress, etPassword;
    private Button btnEditSave;
    private boolean isEditMode = false; // mặc định: chỉ đọc
    private BottomNavigationView bottomNavigation;

    private UserService userService;
    private User currentUser;
    private int userId;

    // SharedPreferences constants (matching LoginActivity)
    private static final String PREF_NAME = "user_prefs";
    private static final String SESSION_PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Retrofit
        initRetrofit();

        // Ánh xạ view
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        btnEditSave = findViewById(R.id.btnEditSave);

        // Get userId from SharedPreferences
        userId = getUserIdFromSharedPreferences();

        // Check if user is logged in
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        // Load user data from API
        loadUserData();

        // Mặc định: disable tất cả
        setEditable(false);

        btnEditSave.setOnClickListener(v -> {
            if (isEditMode) {
                // Chế độ lưu - gọi API để cập nhật
                updateUserData();
            } else {
                // Chuyển sang chế độ chỉnh sửa
                isEditMode = true;
                setEditable(true);
                btnEditSave.setText("Lưu");
            }
        });

        setupBottomNavigation();
    }

    /**
     * Get user ID from SharedPreferences (matching LoginActivity structure)
     * @return userId if found, -1 if not found or not logged in
     */
    private int getUserIdFromSharedPreferences() {
        SharedPreferences sessionPrefs = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sessionPrefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (!isLoggedIn) {
            return -1; // User not logged in
        }

        SharedPreferences userPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return userPrefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Save user session to SharedPreferences (matching LoginActivity structure)
     */
    public static void saveUserSession(android.content.Context context, int userId) {
        SharedPreferences userPrefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.putInt(KEY_USER_ID, userId);
        userEditor.apply();

        SharedPreferences sessionPrefs = context.getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor sessionEditor = sessionPrefs.edit();
        sessionEditor.putBoolean(KEY_IS_LOGGED_IN, true);
        sessionEditor.apply();
    }

    /**
     * Clear user session from SharedPreferences (call this when logging out)
     */
    public static void clearUserSession(android.content.Context context) {
        SharedPreferences userPrefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.clear();
        userEditor.apply();

        SharedPreferences sessionPrefs = context.getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor sessionEditor = sessionPrefs.edit();
        sessionEditor.clear();
        sessionEditor.apply();
    }

    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn(android.content.Context context) {
        SharedPreferences sessionPrefs = context.getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
        return sessionPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Redirect to login activity
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void initRetrofit() {
        String baseUrl = "https://web-production-b71f7.up.railway.app/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
    }

    private void loadUserData() {
        // Show loading state
        btnEditSave.setEnabled(false);
        btnEditSave.setText("Đang tải...");

        Call<User> call = userService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    displayUserData();
                } else {
                    Toast.makeText(UserProfileActivity.this,
                            "Lỗi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    // Load default data as fallback
                    loadDefaultData();
                }
                // Reset button state
                btnEditSave.setEnabled(true);
                btnEditSave.setText("Chỉnh sửa");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Load default data as fallback
                loadDefaultData();
                // Reset button state
                btnEditSave.setEnabled(true);
                btnEditSave.setText("Chỉnh sửa");
            }
        });
    }

    private void displayUserData() {
        if (currentUser != null) {
            etUsername.setText(currentUser.getUsername());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhoneNumber());
            etAddress.setText(currentUser.getAddress());
//            etPassword.setText(currentUser.getPassword());
        }
    }

    private void loadDefaultData() {
        // Fallback to default data if API fails
        etUsername.setText("johnny");
        etEmail.setText("johnny@example.com");
        etPhone.setText("0987654321");
        etAddress.setText("123 Nguyễn Văn Cừ, TP.HCM");
        etPassword.setText("123456");
    }

    private void updateUserData() {
        // Create UpdateUserDto with current form data
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(etEmail.getText().toString().trim());
        updateUserDto.setPhoneNumber(etPhone.getText().toString().trim());
        updateUserDto.setAddress(etAddress.getText().toString().trim());
        // Note: role is not updated from UI, keeping current user's role
        if (currentUser != null) {
            updateUserDto.setRole(currentUser.getRole());
        }

        // Validate input
        if (updateUserDto.getEmail().isEmpty()) {
            Toast.makeText(this, "Vui lòng điền email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        btnEditSave.setEnabled(false);
        btnEditSave.setText("Đang lưu...");

        Call<User> call = userService.updateUser(userId, updateUserDto);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update successful
                    currentUser = response.body();
                    isEditMode = false;
                    setEditable(false);
                    Toast.makeText(UserProfileActivity.this,
                            "Đã lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                    btnEditSave.setText("Chỉnh sửa");
                } else {
                    Toast.makeText(UserProfileActivity.this,
                            "Lỗi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    btnEditSave.setText("Lưu");
                }
                btnEditSave.setEnabled(true);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnEditSave.setText("Lưu");
                btnEditSave.setEnabled(true);
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);

        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_account);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_search) {
                    startActivity(new Intent(this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    // startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    return true;
                }
                return false;
            });
        }
    }

    private void setEditable(boolean enabled) {
        // Username and password are not editable based on your DTO
        etUsername.setEnabled(false); // Username is not in UpdateUserDto
        etEmail.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etAddress.setEnabled(enabled);
        etPassword.setEnabled(false); // Password is not in UpdateUserDto
    }
}