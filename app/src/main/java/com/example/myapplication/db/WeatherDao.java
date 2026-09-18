package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.myapplication.model.WeatherRecord;
import java.util.ArrayList;
import java.util.List;

public class WeatherDao {

    private final DatabaseHelper helper;

    public WeatherDao(Context context) {
        helper = new DatabaseHelper(context);
    }

    public long insert(WeatherRecord record) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("city", record.getCity());
        cv.put("temperature", record.getTemperature());
        cv.put("description", record.getDescription());
        cv.put("humidity", record.getHumidity());
        cv.put("wind", record.getWind());
        cv.put("query_time", record.getQueryTime());
        return db.insert(DatabaseHelper.TABLE_WEATHER, null, cv);
    }

    public List<WeatherRecord> queryAll() {
        List<WeatherRecord> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_WEATHER, null,
                null, null, null, null, "query_time DESC");
        while (c.moveToNext()) {
            WeatherRecord r = new WeatherRecord(
                    c.getString(c.getColumnIndexOrThrow("city")),
                    c.getString(c.getColumnIndexOrThrow("temperature")),
                    c.getString(c.getColumnIndexOrThrow("description")),
                    c.getString(c.getColumnIndexOrThrow("humidity")),
                    c.getString(c.getColumnIndexOrThrow("wind")),
                    c.getLong(c.getColumnIndexOrThrow("query_time"))
            );
            r.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            list.add(r);
        }
        c.close();
        return list;
    }
}