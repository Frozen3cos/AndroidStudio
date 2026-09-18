package com.example.myapplication.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherService {

    public interface Callback {
        void onSuccess(String temperature, String description, String humidity, String wind);
        void onError(String error);
    }

    public static void getWeather(final double lat, final double lon, final Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://wttr.in/" + lat + "," + lon + "?format=j1");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject json = new JSONObject(response.toString());
                    JSONObject current = json.getJSONArray("current_condition").getJSONObject(0);

                    String temp = current.getString("temp_C") + "°C";
                    String desc = current.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                    String humidity = current.getString("humidity") + "%";
                    String wind = current.getString("windspeedKmph") + "km/h";

                    callback.onSuccess(temp, desc, humidity, wind);
                } else {
                    callback.onError("天气请求失败：" + responseCode);
                }
            } catch (Exception e) {
                callback.onError("天气网络错误：" + e.getMessage());
            }
        }).start();
    }
}