package com.hagarsoft.weatherapp.data;

public class WeatherLocation {
    public String name;
    public String weather;
    public String weatherIcon;
    public double tempC;         // Celsius
    public double tempF;         // Fahrenheit
    public double lat;
    public double lon;

    public WeatherLocation(String name, float tempC, String weatherIcon) {
        this.name = name;
        this.tempC = tempC;
        this.weatherIcon = weatherIcon;
    }
}
