package com.example.project.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.project.dto.auth.UserDto;

public class UserManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phoneNumber";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(UserDto user) {
        prefs.edit()
                .putString(KEY_ID, user.getId())
                .putString(KEY_USERNAME, user.getUsername())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_PHONE, user.getPhoneNumber())
                .putString(KEY_ADDRESS, user.getAddress())
                .putString(KEY_ROLE, user.getRole())
                .apply();
    }

    public UserDto getUser() {
        UserDto user = new UserDto();
        user.setId(prefs.getString(KEY_ID, null));
        user.setUsername(prefs.getString(KEY_USERNAME, null));
        user.setEmail(prefs.getString(KEY_EMAIL, null));
        user.setPhoneNumber(prefs.getString(KEY_PHONE, null));
        user.setAddress(prefs.getString(KEY_ADDRESS, null));
        user.setRole(prefs.getString(KEY_ROLE, null));
        return user;
    }

    public void clearUser() {
        prefs.edit().clear().apply();
    }
}
