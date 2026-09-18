package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息数据访问对象
 */
public class MessageDao {

    private final DatabaseHelper helper;

    public MessageDao(Context context) {
        helper = new DatabaseHelper(context);
    }

    /** 增：插入一条消息 */
    public long insert(String friendName, String content, boolean fromMe) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("friend_name", friendName);
        cv.put("content", content);
        cv.put("is_from_me", fromMe ? 1 : 0);
        cv.put("timestamp", System.currentTimeMillis());
        return db.insert(DatabaseHelper.TABLE_MESSAGES, null, cv);
    }

    /** 删：删除某好友的全部消息 */
    public int deleteByFriend(String friendName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_MESSAGES,
                "friend_name = ?", new String[]{friendName});
    }

    /** 查：获取与某好友的全部聊天记录（按时间升序） */
    public List<ChatMessage> queryByFriend(String friendName) {
        List<ChatMessage> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_MESSAGES, null,
                "friend_name = ?", new String[]{friendName},
                null, null, "timestamp ASC");
        while (c.moveToNext()) {
            list.add(cursorToMessage(c));
        }
        c.close();
        return list;
    }

    /** 查：获取所有会话（每个好友的最后一条消息，按时间倒序） */
    public List<ChatMessage> queryConversations() {
        List<ChatMessage> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_MESSAGES +
                " WHERE id IN (" +
                " SELECT MAX(id) FROM " + DatabaseHelper.TABLE_MESSAGES +
                " GROUP BY friend_name) " +
                " ORDER BY timestamp DESC";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            list.add(cursorToMessage(c));
        }
        c.close();
        return list;
    }

    /** 查：统计我发出的消息条数 */
    public int countSentByMe() {
        return countByCondition("is_from_me = 1");
    }

    /** 查：统计我收到的消息条数 */
    public int countReceived() {
        return countByCondition("is_from_me = 0");
    }

    /** 查：最近 N 天的每日消息数（index 0 是最早的那天，末尾是今天） */
    public int[] getDailyMessageCount(int days) {
        int[] result = new int[days];
        long now = System.currentTimeMillis();
        long oneDay = 24L * 60 * 60 * 1000;

        SQLiteDatabase db = helper.getReadableDatabase();
        for (int i = 0; i < days; i++) {
            long startOfDay = now - (long)(days - 1 - i) * oneDay;
            long endOfDay = startOfDay + oneDay;
            Cursor c = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_MESSAGES +
                            " WHERE timestamp >= ? AND timestamp < ?",
                    new String[]{String.valueOf(startOfDay), String.valueOf(endOfDay)});
            if (c.moveToFirst()) result[i] = c.getInt(0);
            c.close();
        }
        return result;
    }

    /** 通用条件统计 */
    private int countByCondition(String where) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_MESSAGES +
                " WHERE " + where, null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    /** 游标转 ChatMessage */
    private ChatMessage cursorToMessage(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow("id"));
        String name = c.getString(c.getColumnIndexOrThrow("friend_name"));
        String content = c.getString(c.getColumnIndexOrThrow("content"));
        boolean fromMe = c.getInt(c.getColumnIndexOrThrow("is_from_me")) == 1;
        long ts = c.getLong(c.getColumnIndexOrThrow("timestamp"));
        return new ChatMessage(id, name, content, fromMe, ts);
    }
}