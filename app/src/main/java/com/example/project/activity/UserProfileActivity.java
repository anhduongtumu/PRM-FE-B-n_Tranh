package com.example.project.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project.R;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etAddress, etPassword;
    private Button btnEditSave;
    private boolean isEditMode = false; // mặc định: chỉ đọc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ánh xạ view
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        btnEditSave = findViewById(R.id.btnEditSave);

        // Thiết lập dữ liệu mẫu
        etUsername.setText("johnny");
        etEmail.setText("johnny@example.com");
        etPhone.setText("0987654321");
        etAddress.setText("123 Nguyễn Văn Cừ, TP.HCM");
        etPassword.setText("123456");

        // Mặc định: disable tất cả
        setEditable(false);

        btnEditSave.setOnClickListener(v -> {
            if (isEditMode) {
                // Chế độ lưu
                isEditMode = false;
                setEditable(false);
                Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                btnEditSave.setText("Chỉnh sửa");

                // TODO: Lưu vào server hoặc SharedPreferences nếu muốn

            } else {
                // Chuyển sang chế độ chỉnh sửa
                isEditMode = true;
                setEditable(true);
                btnEditSave.setText("Lưu");
            }
        });
    }

    private void setEditable(boolean enabled) {
        etUsername.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etAddress.setEnabled(enabled);
        etPassword.setEnabled(enabled);
    }
}
