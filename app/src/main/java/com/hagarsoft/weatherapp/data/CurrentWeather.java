package com.hagarsoft.weatherapp.data;

import com.hagarsoft.weatherapp.util.StringDumpUtils;

public class CurrentWeather {
    public Coord coord;
    public Sys sys;
    public Weather weather[];
    public Main main;
    public Wind wind;
    public Rain rain;
    public Clouds clouds;
    public int visibility;
    public long dt;
    public int id;
    public String name;

    @Override
    public String toString() {
        return StringDumpUtils.dump(this);
    }
}