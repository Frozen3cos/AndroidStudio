package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** 用户数据访问对象：增删查改 */
public class UserDao {

    private final DatabaseHelper helper;

    public UserDao(Context context) {
        helper = new DatabaseHelper(context);
    }

    /** 增：注册新用户 */
    public boolean insert(String username, String password,
                          String phone, String email, int avatarIndex) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("avatar_index", avatarIndex);
        cv.put("create_time", System.currentTimeMillis());
        long rowId = db.insert(DatabaseHelper.TABLE_USERS, null, cv);
        return rowId > 0;
    }

    /** 删：删除用户 */
    public boolean delete(String username) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_USERS,
                "username = ?", new String[]{username});
        return rows > 0;
    }

    /** 改：修改密码 */
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update(DatabaseHelper.TABLE_USERS, cv,
                "username = ?", new String[]{username});
        return rows > 0;
    }

    /** 查：判断用户是否存在 */
    public boolean exists(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_USERS, new String[]{"id"},
                "username = ?", new String[]{username},
                null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    /** 查：校验登录（用户名+密码） */
    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_USERS, new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null);
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    /** 查：获取用户的头像下标 */
    public int getAvatarIndex(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_USERS, new String[]{"avatar_index"},
                "username = ?", new String[]{username},
                null, null, null);
        int idx = 0;
        if (c.moveToFirst()) idx = c.getInt(0);
        c.close();
        return idx;
    }

    /** 查：获取用户总数（图表展示用） */
    public int getTotalCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }
}