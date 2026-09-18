package com.example.myapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.myapplication.R;
import com.example.myapplication.db.WeatherDao;
import com.example.myapplication.model.WeatherRecord;
import com.example.myapplication.network.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLocation, tvWeather;
    private WeatherDao weatherDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        tvLocation = findViewById(R.id.tvLocation);
        tvWeather = findViewById(R.id.tvWeather);
        weatherDao = new WeatherDao(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                tvLocation.setText("未授予定位权限");
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        // 逆地理编码获取地址
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String address = addresses.get(0).getAddressLine(0);
                                tvLocation.setText("当前位置：" + address);
                            } else {
                                tvLocation.setText("当前位置：纬度 " + lat + "，经度 " + lon);
                            }
                        } catch (IOException e) {
                            tvLocation.setText("当前位置：纬度 " + lat + "，经度 " + lon);
                        }

                        // 查询天气并存入数据库
                        queryWeather(lat, lon);
                    } else {
                        tvLocation.setText("无法获取当前位置");
                    }
                });
    }

    private void queryWeather(double lat, double lon) {
        WeatherService.getWeather(lat, lon, new WeatherService.Callback() {
            @Override
            public void onSuccess(String temperature, String description, String humidity, String wind) {
                runOnUiThread(() -> {
                    String info = "温度：" + temperature + "\n天气：" + description +
                            "\n湿度：" + humidity + "\n风速：" + wind;
                    tvWeather.setText(info);

                    // 存入数据库
                    WeatherRecord record = new WeatherRecord("当前位置",
                            temperature, description, humidity, wind,
                            System.currentTimeMillis());
                    weatherDao.insert(record);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> tvWeather.setText("天气获取失败：" + error));
            }
        });
    }
}