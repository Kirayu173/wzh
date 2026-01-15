package com.wzh.suyuan.data.auth;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.wzh.suyuan.kit.SpUtils;
import com.wzh.suyuan.network.model.AuthUser;

public final class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_EXPIRE_AT = "auth_expire_at";
    private static final String KEY_USER_ID = "auth_user_id";
    private static final String KEY_USERNAME = "auth_username";
    private static final String KEY_ROLE = "auth_role";

    private AuthManager() {
    }

    public static boolean hasToken(Context context) {
        return !TextUtils.isEmpty(getToken(context));
    }

    public static String getToken(Context context) {
        return SpUtils.getString(context, KEY_TOKEN, "");
    }

    public static long getExpireAt(Context context) {
        return SpUtils.getLong(context, KEY_EXPIRE_AT, 0L);
    }

    public static void saveSession(Context context, String token, long expireAt, AuthUser user) {
        SpUtils.putString(context, KEY_TOKEN, token == null ? "" : token);
        SpUtils.putLong(context, KEY_EXPIRE_AT, expireAt);
        if (user != null) {
            saveUser(context, user);
        }
        Log.i(TAG, "saveSession token=" + maskToken(token) + ", expireAt=" + expireAt);
    }

    public static void saveUser(Context context, AuthUser user) {
        if (user == null) {
            return;
        }
        Long userId = user.getId();
        SpUtils.putLong(context, KEY_USER_ID, userId == null ? 0L : userId);
        SpUtils.putString(context, KEY_USERNAME, safeString(user.getUsername()));
        SpUtils.putString(context, KEY_ROLE, safeString(user.getRole()));
        Log.i(TAG, "saveUser userId=" + maskUserId(userId));
    }

    public static AuthUser getUser(Context context) {
        long userId = SpUtils.getLong(context, KEY_USER_ID, 0L);
        String username = SpUtils.getString(context, KEY_USERNAME, "");
        String role = SpUtils.getString(context, KEY_ROLE, "");
        if (userId == 0 && TextUtils.isEmpty(username)) {
            return null;
        }
        AuthUser user = new AuthUser();
        user.setId(userId);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    public static void clearSession(Context context) {
        SpUtils.putString(context, KEY_TOKEN, "");
        SpUtils.putLong(context, KEY_EXPIRE_AT, 0L);
        SpUtils.putLong(context, KEY_USER_ID, 0L);
        SpUtils.putString(context, KEY_USERNAME, "");
        SpUtils.putString(context, KEY_ROLE, "");
        Log.i(TAG, "clearSession");
    }

    private static String safeString(String value) {
        return value == null ? "" : value;
    }

    private static String maskToken(String token) {
        if (TextUtils.isEmpty(token) || token.length() < 8) {
            return "****";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }

    private static String maskUserId(Long userId) {
        if (userId == null) {
            return "***";
        }
        String value = String.valueOf(userId);
        if (value.length() <= 2) {
            return "***" + value;
        }
        return "***" + value.substring(value.length() - 2);
    }
}
