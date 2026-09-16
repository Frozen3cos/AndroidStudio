package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户数据存储类
 * 使用 SharedPreferences 保存所有注册用户的信息（演示用，实际应用应使用数据库）
 * 存储结构：
 *   - "all_users" : Set<String>，保存所有已注册的用户名
 *   - "user_{username}_password" : String，保存该用户的密码
 *   - "user_{username}_phone"    : String，保存手机号
 *   - "user_{username}_email"    : String，保存邮箱
 *   - "user_{username}_avatar"   : int，保存头像下标
 */
public class UserStore {

    private static final String SP_NAME = "user_store";
    private static final String KEY_ALL_USERS = "all_users";
    private static final String SUFFIX_PWD = "_password";
    private static final String SUFFIX_PHONE = "_phone";
    private static final String SUFFIX_EMAIL = "_email";
    private static final String SUFFIX_AVATAR = "_avatar";

    private final SharedPreferences sp;

    public UserStore(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /** 检查用户名是否已注册 */
    public boolean isUserExists(String username) {
        return getAllUsers().contains(username);
    }

    /** 注册用户 */
    public boolean register(String username, String password,
                            String phone, String email, int avatarIndex) {
        if (isUserExists(username)) return false;

        Set<String> users = getAllUsers();
        users.add(username);

        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(KEY_ALL_USERS, users);
        editor.putString(username + SUFFIX_PWD, password);
        editor.putString(username + SUFFIX_PHONE, phone);
        editor.putString(username + SUFFIX_EMAIL, email);
        editor.putInt(username + SUFFIX_AVATAR, avatarIndex);
        editor.apply();
        return true;
    }

    /** 校验登录 */
    public boolean validateLogin(String username, String password) {
        if (!isUserExists(username)) return false;
        String savedPwd = sp.getString(username + SUFFIX_PWD, "");
        return savedPwd.equals(password);
    }

    public String getPhone(String username) {
        return sp.getString(username + SUFFIX_PHONE, "");
    }

    public String getEmail(String username) {
        return sp.getString(username + SUFFIX_EMAIL, "");
    }

    public int getAvatarIndex(String username) {
        return sp.getInt(username + SUFFIX_AVATAR, 0);
    }

    private Set<String> getAllUsers() {
        // 注意：putStringSet 后不能再修改返回的 Set，所以要 copy
        return new HashSet<>(sp.getStringSet(KEY_ALL_USERS, new HashSet<>()));
    }
}