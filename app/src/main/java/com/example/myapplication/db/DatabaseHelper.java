package com.example.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "you_liao.db";
    public static final int DB_VERSION = 1;

    // 表名
    public static final String TABLE_USERS = "users";
    public static final String TABLE_FRIENDS = "friends";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_WEATHER = "weather";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT, " +
                "avatar_index INTEGER DEFAULT 0, " +
                "create_time INTEGER)");

        // 好友表
        db.execSQL("CREATE TABLE " + TABLE_FRIENDS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "avatar_index INTEGER DEFAULT 0, " +
                "signature TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "gender TEXT)");

        // 消息表
        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "friend_name TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "is_from_me INTEGER DEFAULT 0, " +
                "timestamp INTEGER)");

        // 天气查询历史
        db.execSQL("CREATE TABLE " + TABLE_WEATHER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "city TEXT, " +
                "temperature TEXT, " +
                "description TEXT, " +
                "humidity TEXT, " +
                "wind TEXT, " +
                "query_time INTEGER)");

        // 首次创建时插入默认好友数据
        seedDefaultFriends(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
        onCreate(db);
    }

    /** 首次创建插入 8 个演示好友 */
    private void seedDefaultFriends(SQLiteDatabase db) {
        String[][] friends = {
                {"小明", "0", "热爱编程的男孩", "13800000001", "xiaoming@mail.com", "男"},
                {"小红", "1", "每天都要开心哦", "13800000002", "xiaohong@mail.com", "女"},
                {"小刚", "2", "运动使我快乐", "13800000003", "xiaogang@mail.com", "男"},
                {"小美", "3", "美食探店达人", "13800000004", "xiaomei@mail.com", "女"},
                {"阿杰", "0", "音乐是生命的一部分", "13800000005", "ajie@mail.com", "男"},
                {"莉莉", "1", "旅行者，摄影爱好者", "13800000006", "lili@mail.com", "女"},
                {"大壮", "2", "健身教练", "13800000007", "dazhuang@mail.com", "男"},
                {"悠悠", "3", "插画师", "13800000008", "youyou@mail.com", "女"}
        };
        for (String[] f : friends) {
            db.execSQL("INSERT INTO " + TABLE_FRIENDS +
                            " (name, avatar_index, signature, phone, email, gender) VALUES (?,?,?,?,?,?)",
                    f);
        }
    }
}