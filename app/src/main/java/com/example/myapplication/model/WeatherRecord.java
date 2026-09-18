package com.example.myapplication.model;

public class WeatherRecord {
    private int id;
    private String city;
    private String temperature;
    private String description;
    private String humidity;
    private String wind;
    private long queryTime;

    public WeatherRecord(String city, String temperature, String description,
                         String humidity, String wind, long queryTime) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.wind = wind;
        this.queryTime = queryTime;
    }

    // Getters
    public int getId() { return id; }
    public String getCity() { return city; }
    public String getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public String getHumidity() { return humidity; }
    public String getWind() { return wind; }
    public long getQueryTime() { return queryTime; }

    public void setId(int id) { this.id = id; }
}