package com.hagarsoft.weatherapp.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WeatherLocationsRepository {
    private static final String TAG = "WeatherLocationsRepository";

    private List<WeatherLocation> locations;

    public WeatherLocationsRepository() {
        locations = new ArrayList<>();
        createLocationDetailsMap();
    }

    public List<WeatherLocation> getLocations() {
        return locations;
    }

    public WeatherLocation getLocation(int i) {
        return locations.get(i);
    }

    public void addLocation(WeatherLocation location) {
        locations.add(location);
        Log.d(TAG, "Added location. Location count = " + locations.size());
    }

    public void deleteLocation(int i) {
        locations.remove(i);
        Log.d(TAG, "Deleted location. Location count = " + locations.size());
    }

    private void createLocationDetailsMap() {
        locations.add(new WeatherLocation("Tokyo", 35.6895, 139.6917));
        locations.add(new WeatherLocation("London", 51.5074, -0.1278));
        locations.add(new WeatherLocation("New York", 40.7128, -74.0060));
    }

}