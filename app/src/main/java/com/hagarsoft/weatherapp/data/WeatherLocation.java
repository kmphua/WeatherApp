package com.hagarsoft.weatherapp.data;

public class WeatherLocation {
    private String name;
    private double lat;
    private double lon;
    private String weather;
    private String weatherIcon;
    private double tempCelsius;
    private double tempFahrenheit;

    public WeatherLocation(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public double getTempCeisius() {
        return tempCelsius;
    }

    public void setTempCelsius(double tempCelsius) {
        this.tempCelsius = tempCelsius;
        this.tempFahrenheit = tempCelsius * 5/9 + 32;
    }

    public double getTempFahrenheit() {
        return tempFahrenheit;
    }

    public void setTempFahrenheit(double tempFahrenheit) {
        this.tempFahrenheit = tempFahrenheit;
        this.tempCelsius =  ((tempFahrenheit - 32)*5)/9;
    }
}
