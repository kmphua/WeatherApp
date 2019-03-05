package com.hagarsoft.weatherapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hagarsoft.weatherapp.data.WeatherLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataStoreUtil {
    private final static String TAG = "DataStoreUtil";

    // Shared preferences file that is used to save location list.
    private final static String SHARED_PREFERENCES_FILE_LOCATION_LIST = "locationList";

    // Saved location list json string key in shared preferences file..
    private final static String SHARED_PREFERENCES_KEY_LOCATION_LIST = "Location_List";

    public static List<WeatherLocation> readLocationData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_LOCATION_LIST, MODE_PRIVATE);

        // Get saved string data in it.
        String locationListJsonString = sharedPreferences.getString(SHARED_PREFERENCES_KEY_LOCATION_LIST, "");

        // Create Gson object and translate the json string to related java object array.
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<WeatherLocation>>(){}.getType();
        return gson.fromJson(locationListJsonString, listType);
    }

    public static void saveLocationData(Context context, List<WeatherLocation> locationList) {
        Gson gson = new Gson();

        // Get java object list json format string.
        String locationListJsonString = gson.toJson(locationList);

        // Create SharedPreferences object.
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_LOCATION_LIST, MODE_PRIVATE);

        // Put the json format string to SharedPreferences object.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_KEY_LOCATION_LIST, locationListJsonString);
        editor.apply();
    }

    public static boolean addWeatherLocation(WeatherLocation location) {
        return false;
    }

    public static boolean removeWeatherLocation(WeatherLocation location) {
        return false;
    }
}
