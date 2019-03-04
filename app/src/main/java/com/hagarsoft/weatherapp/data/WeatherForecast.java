package com.hagarsoft.weatherapp.data;

import com.hagarsoft.weatherapp.util.StringDumpUtils;

public class WeatherForecast {
    public int count;
    public WeatherForecastItem list[];
    public City city;

    @Override
    public String toString() {
        return StringDumpUtils.dump(this);
    }
}