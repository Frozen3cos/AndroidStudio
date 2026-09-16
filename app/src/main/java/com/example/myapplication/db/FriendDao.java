package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendDao {

    private final DatabaseHelper helper;

    public FriendDao(Context context) {
        helper = new DatabaseHelper(context);
    }

    /** 增：添加好友 */
    public long insert(Friend f) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", f.getName());
        cv.put("avatar_index", f.getAvatarIndex());
        cv.put("signature", f.getSignature());
        cv.put("phone", f.getPhone());
        cv.put("email", f.getEmail());
        cv.put("gender", f.getGender());
        return db.insert(DatabaseHelper.TABLE_FRIENDS, null, cv);
    }

    /** 删：删除好友 */
    public boolean delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_FRIENDS, "id = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    /** 改：修改好友签名 */
    public boolean updateSignature(int id, String newSignature) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("signature", newSignature);
        return db.update(DatabaseHelper.TABLE_FRIENDS, cv,
                "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /** 查：全部好友 */
    public List<Friend> queryAll() {
        List<Friend> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_FRIENDS, null,
                null, null, null, null, "id ASC");
        while (c.moveToNext()) {
            Friend f = cursorToFriend(c);
            list.add(f);
        }
        c.close();
        return list;
    }

    /** 查：按名字搜索（模糊匹配） */
    public List<Friend> searchByName(String keyword) {
        List<Friend> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_FRIENDS, null,
                "name LIKE ?", new String[]{"%" + keyword + "%"},
                null, null, "id ASC");
        while (c.moveToNext()) list.add(cursorToFriend(c));
        c.close();
        return list;
    }

    /** 查：按性别统计（图表用） */
    public int countByGender(String gender) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_FRIENDS +
                " WHERE gender = ?", new String[]{gender});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    /** 查：总好友数 */
    public int getTotalCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_FRIENDS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    private Friend cursorToFriend(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow("id"));
        String name = c.getString(c.getColumnIndexOrThrow("name"));
        int avatarIndex = c.getInt(c.getColumnIndexOrThrow("avatar_index"));
        String signature = c.getString(c.getColumnIndexOrThrow("signature"));
        String phone = c.getString(c.getColumnIndexOrThrow("phone"));
        String email = c.getString(c.getColumnIndexOrThrow("email"));
        String gender = c.getString(c.getColumnIndexOrThrow("gender"));

        Friend f = new Friend(name, avatarIndex, signature, phone, email, gender);
        f.setId(id);
        return f;
    }
}