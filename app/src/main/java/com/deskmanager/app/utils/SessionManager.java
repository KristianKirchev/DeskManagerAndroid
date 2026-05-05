package com.deskmanager.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.deskmanager.app.domain.enums.UserRole;

public class SessionManager {

    private static final String PREF_NAME  = "desk_manager_session";
    private static final String KEY_USER_ID   = "user_id";
    private static final String KEY_USERNAME  = "username";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_ROLE      = "role";
    private static final String KEY_MUST_CHANGE_PASSWORD = "must_change_password";
    private static final int    NO_USER = -1;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(int userId, String username, String name, UserRole role) {
        prefs.edit()
             .putInt(KEY_USER_ID, userId)
             .putString(KEY_USERNAME, username)
             .putString(KEY_USER_NAME, name)
             .putString(KEY_ROLE, role.name())
             .apply();
    }

    public void setMustChangePassword(boolean value) {
        prefs.edit().putBoolean(KEY_MUST_CHANGE_PASSWORD, value).apply();
    }

    public boolean mustChangePassword() {
        return prefs.getBoolean(KEY_MUST_CHANGE_PASSWORD, false);
    }

    public boolean isLoggedIn() {
        return prefs.getInt(KEY_USER_ID, NO_USER) != NO_USER;
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, NO_USER);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public UserRole getUserRole() {
        String roleStr = prefs.getString(KEY_ROLE, UserRole.GUEST.name());
        return UserRole.valueOf(roleStr);
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
