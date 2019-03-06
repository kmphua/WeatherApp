package com.hagarsoft.weatherapp.data;

import android.content.Context;
import android.util.Log;

import com.hagarsoft.weatherapp.util.DataStoreUtil;

import java.util.ArrayList;
import java.util.List;

public class WeatherLocationsRepository {
    private static final String TAG = "WeatherLocationsRepository";

    private List<WeatherLocation> mLocations;
    private Context mContext;

    public WeatherLocationsRepository(Context context) {
        mLocations = new ArrayList<>();
        mContext = context;
        createLocationDetailsMap();
    }

    public List<WeatherLocation> getLocations() {
        return mLocations;
    }

    public WeatherLocation getLocation(int i) {
        return mLocations.get(i);
    }

    public void addLocation(WeatherLocation location) {
        mLocations.add(location);
        DataStoreUtil.saveLocationData(mContext, mLocations);
    }

    public void deleteLocation(int i) {
        mLocations.remove(i);
        DataStoreUtil.saveLocationData(mContext, mLocations);
    }

    private void createLocationDetailsMap() {
        mLocations = DataStoreUtil.readLocationData(mContext);
    }
}